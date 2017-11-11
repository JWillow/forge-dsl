package com.probtp.forge.dsl.xml

import groovy.xml.StreamingMarkupBuilder
import spock.lang.Specification

class PathSpec extends Specification {

    Node buildNode(Closure closure) {
        def xml = new StreamingMarkupBuilder().bind(closure).toString()
        return new XmlParser().parseText(xml)
    }

    def "Path return empty list if it found nothing"() {
        when:
        def path = Path.create("bean")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency ("payload")
                    }
                }
            }
        })

        then:
        path.get(node).isEmpty()
    }

    def "Search existing node"() {
        when:
        def path = Path.create("dependencies")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency ("payload")
                    }
                }
            }
        })
        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].parent().name() == "dependencyManagement"
        nodes[0].children().size() == 1
        nodes[0].children()[0].name() == "dependency"
    }

    def "Search existing nodes in different parent"() {
        when:
        def path = Path.create("dependency.artifactId")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId("dartifact1")
                            version("2.0")
                        }
                        dependency {
                            artifactId("artifact2")
                            version("2.0")
                        }
                    }
                }
            }
        })
        then:
        NodeList nodes = path.get(node)
        nodes.size() == 2
        nodes[0].text() == "dartifact1"
        nodes[1].text() == "artifact2"
        nodes[0].parent() != nodes[1].parent()
        nodes[0].parent().name() == "dependency"
        nodes[1].parent().name() == "dependency"
        nodes[0].parent().parent() == nodes[1].parent().parent()
    }

    def "Search existing nodes in different parent, with other parent hierarchy"() {
        when:
        def path = Path.create("dependency.artifactId")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId("dartifact1")
                            version("2.0")
                        }
                        dependency {
                            artifactId("artifact2")
                            version("2.0")
                        }
                    }
                }
                dependencies {
                    dependency {
                        artifactId("artifact3")
                        version("5.0")
                    }
                }
            }
        })
        then:
        NodeList nodes = path.get(node)
        nodes.size() == 3
        nodes[0].text() == "dartifact1"
        nodes[1].text() == "artifact2"
        nodes[2].text() == "artifact3"
    }

    def "Search existing node, one true path"() {
        when:
        def path = Path.create("project.dependencies.dependency")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId("dartifact1")
                            version("2.0")
                        }
                        dependency {
                            artifactId("artifact2")
                            version("2.0")
                        }
                    }
                }
                dependencies {
                    dependency {
                        artifactId("artifact3")
                        version("5.0")
                    }
                }
            }
        })
        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].artifactId.text() == "artifact3"
    }

    def "Search existing node, dependencyManagement path"() {
        when:
        def path = Path.create("dependencyManagement.dependencies.dependency")
        def node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId("artifact1")
                            version("2.0")
                        }
                        dependency {
                            artifactId("artifact2")
                            version("2.0")
                        }
                    }
                }
                dependencies {
                    dependency {
                        artifactId("artifact3")
                        version("5.0")
                    }
                }
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 2
        nodes[0].artifactId.text() == "artifact1"
        nodes[1].artifactId.text() == "artifact2"
    }

    def "Search not existing parent node"() {
        when:
        def path = Path.create("dependencyManagement?.dependencies")
        def node = buildNode({
            project {
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].name() == "dependencies"
        nodes[0].parent().name() == "dependencyManagement"
        nodes[0].parent().parent().name() == "project"
        nodes[0].children().size() == 0
    }

    def "Search not existing child node"() {
        when:
        def path = Path.create("dependencyManagement.dependencies?")
        def node = buildNode({
            project {
                dependencyManagement {}
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].name() == "dependencies"
        nodes[0].parent().name() == "dependencyManagement"
        nodes[0].parent().parent().name() == "project"
        nodes[0].children().size() == 0
    }

    def "node properties with child with dot in name"() {
        when:
        def path = Path.create("properties")
        def node = buildNode({
            project {
                properties {
                    'project.build.sourceEncoding'('UTF-8')
                }
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].children()[0].text() == "UTF-8"
    }

    def "node syntax dot"() {
        when:
        def path = Path.create("properties.'project.build.sourceEncoding'")
        def node = buildNode({
            project {
                properties {
                    'project.build.sourceEncoding'('UTF-8')
                }
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].text() == "UTF-8"
    }

    def "node syntax dot, optional mode enable"() {
        when:
        def path = Path.create("properties.'project.build.sourceEncoding'?")
        def node = buildNode({
            project {
                properties {
                }
            }
        })

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].parent().name() == "properties"
    }

    def "node syntax dot, from file"() {
        setup:
        Node node = new XmlParser().parse(new File('src/test/resources/xml/pomTest.xml'))

        when:
        def path = Path.create("properties.'project.build.sourceEncoding'")

        then:
        NodeList nodes = path.get(node)
        nodes.size() == 1
        nodes[0].text() == "UTF-8"
    }

    def "with namespace, spring file" () {

        setup:
        Node node = new XmlParser().parse(new File('src/test/resources/xml/services-config.xml'))

        when:
        def path = Path.create("jee:jndi-lookup")

        then:
        NodeList nodes = path.get(node)
        // <tx:annotation-driven transaction-manager="transactionManager" />
        nodes[0]['@id'] == "entityManagerFactory"
    }
}
