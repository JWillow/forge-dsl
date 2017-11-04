package com.probtp.forge.dsl.xml

import groovy.xml.QName
import groovy.xml.StreamingMarkupBuilder
import spock.lang.Specification

class XMLOperationSpec extends Specification {

    Node buildNode(Closure closure) {
        def xml = new StreamingMarkupBuilder().bind(closure).toString()
        return new XmlParser().parseText(xml)
    }

    void show(Node node) {
        XmlNodePrinter nodePrinter = new XmlNodePrinter()
        nodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            namespaceAware = true
            quote = "'" // Use single quote for attributes
        }
        nodePrinter.print(node)
    }

    def "Append simple node"() {
        setup :
        Node node = buildNode({

            project {
                dependencyManagement {
                    dependencies {

                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies")

        when:
        xmlOperation.append {
                dependency {
                    artifactId('mon artifact')
                    version('1.0.0')
                }
        }

        then:
        node.dependencyManagement[0].dependencies[0].dependency[0].artifactId[0].text() == "mon artifact"
        node.dependencyManagement[0].dependencies[0].dependency[0].version[0].text() == "1.0.0"
    }

    def "Append two nodes"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {

                    }
                }
                dependencies {

                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies")

        when:
        xmlOperation.append {
            dependency {
                artifactId('mon artifact')
                version('1.0.0')
            }
        }

        then:
        node.dependencyManagement[0].dependencies[0].dependency[0].artifactId[0].text() == "mon artifact"
        node.dependencyManagement[0].dependencies[0].dependency[0].version[0].text() == "1.0.0"
        node.dependencies[0].dependency[0].artifactId[0].text() == "mon artifact"
        node.dependencies[0].dependency[0].version[0].text() == "1.0.0"
    }

    def "Append on non existing nodes"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {

                    }
                }
                dependencies {

                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "plugins")

        when:
        xmlOperation.append {
            dependency {
                artifactId('mon artifact')
                version('1.0.0')
            }
        }

        then:
        xmlOperation.nodes.isEmpty()
        node.'**'.plugins.isEmpty()
    }

    def "grep, artifactId found"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            artifactId('artifact1')
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].parent().name() == 'dependencies'
        otherXMLOperation.nodes[0].children()[0].name() == 'artifactId'
        otherXMLOperation.nodes[0].children()[0].text() == 'artifact1'
        otherXMLOperation.nodes[0].children()[1].name() == 'version'
        otherXMLOperation.nodes[0].children()[1].text() == '3.0.0'
    }

    def "grep, on non existing path"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "plugins.plugin")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            pluginId('plugin1')
        }

        then:
        otherXMLOperation.nodes.size() == 0
    }

    def "grep, artifactId found with parent tag"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            dependency {
                artifactId('artifact1')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].parent().name() == 'dependencies'
        otherXMLOperation.nodes[0].children()[0].name() == 'artifactId'
        otherXMLOperation.nodes[0].children()[0].text() == 'artifact1'
        otherXMLOperation.nodes[0].children()[1].name() == 'version'
        otherXMLOperation.nodes[0].children()[1].text() == '3.0.0'
    }

    def "grep, artifactId not found"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            artifactId('artifact1NotFound')
        }

        then:
        otherXMLOperation.nodes.size() == 0
    }

    def "grep, artifactId not found based on multi criteria"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            dependency {
                artifactId('artifact2')
                version('2.0.0')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 0
    }

    def "grep, artifactId found based on multi criteria"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                            groupId('test')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            dependency {
                artifactId('artifact2')
                version('1.0.0')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].groupId[0].text() == 'test'
    }

    def "Multiple transformation on existing node"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                            groupId('test')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.transform {
            dependency {
                version('4.0.0')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 2
        otherXMLOperation.nodes[0].version[0].text() == '4.0.0'
        otherXMLOperation.nodes[1].version[0].text() == '4.0.0'
    }

    def "Multiple transformation on non existing node, nothing is transform"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.transform {
            dependency {
                version('4.0.0')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 0
    }

    def "Multiple transformation on non existing node, but optional mode is enable"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies?.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.transform {
            dependency {
                version('4.0.0')
            }
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].version[0].text() == "4.0.0"
    }

    def "RemoveAll dependency nodes"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                            groupId('test')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.removeAll()

        then:
        otherXMLOperation.nodes.size() == 0
        node.dependencyManagement[0].dependencies[0].children().isEmpty()
    }

    def "RemoveAll non existing nodes"() {
        setup :
        Node node = buildNode({
            project {
                dependencyManagement {
                    dependencies {
                        dependency {
                            artifactId('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            artifactId('artifact2')
                            version('1.0.0')
                            groupId('test')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "plugins.plugin")

        when:
        XMLOperation otherXMLOperation = xmlOperation.removeAll()

        then:
        otherXMLOperation.nodes.size() == 0
        node.dependencyManagement[0].dependencies[0].children().size() == 2
    }

    def "Append with namespace"(){
        setup:
        Node node = new XmlParser().parse(new File("./src/test/resources/services-config.xml"))
        Node otherNode = buildNode({
            mkp.declareNamespace( tx: "http://www.springframework.org/schema/tx" )
            project {
                mkp.yield("testremy")
                'dependencyManagement' {
                    dependencies {
                        dependency {
                            'tx:artifactId'('artifact1')
                            version('3.0.0')
                        }
                        dependency {
                            'tx:artifactId'('artifact2')
                            version('1.0.0')
                            groupId('test')
                        }
                    }
                }
            }
        })

        when:
        println "PRINT"
        /*println "1 " + node.name().getLocalPart()
        println "2 " + node.name().getNamespaceURI()
        println "3 " + node.name().getPrefix()
        println "4 " + node.name().getQualifiedName()
        println "Test"
        println node.@xsi*/

        then:
        show(otherNode)
           node != null

    }

    def "Append on spring xml file with namespace"() {
        setup :
        Node node = new XmlParser().parse(new File("./src/test/resources/services-config.xml"))
        XMLOperation xmlOperation = XMLOperation.create(node, "beans")

        when:
        xmlOperation.append {
            mkp.declareNamespace( tx: "http://www.springframework.org/schema/tx@http://www.springframework.org/schema/tx/spring-tx.xsd" )
            'tx:annotation-driven'('transaction-manager':'transactionManagerRLE')
        }

        then:
        show(node)
    }

}
