package com.probtp.forge.dsl.xml

import groovy.xml.QName
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import spock.lang.Specification

class XMLOperationSpec extends Specification {

    Node buildNode(Closure closure) {
        def xml = new StreamingMarkupBuilder().bind(closure).toString()
        return new XmlParser().parseText(xml)
    }

    def "Append simple node"() {
        setup:
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

    def "Append file to node"() {
        setup:
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
        xmlOperation.append "src/test/resources/xml/XMLOperationSpec/dependency.xml" as File

        then:
        node.dependencyManagement[0].dependencies[0].dependency[0].artifactId[0].text() == "groovy-all"
        node.dependencyManagement[0].dependencies[0].dependency[0].version[0].text() == "2.4.12"
    }

    def "Append file template to node"() {
        setup:
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
        xmlOperation.append("src/test/resources/xml/XMLOperationSpec/dependency_template.xml" as File, [version: 1.0])

        then:
        node.dependencyManagement[0].dependencies[0].dependency[0].artifactId[0].text() == "groovy-all"
        node.dependencyManagement[0].dependencies[0].dependency[0].version[0].text() == "1.0"
    }

    def "Append two nodes"() {
        setup:
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
        setup:
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
        setup:
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
        setup:
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
        setup:
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
        setup:
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

    def "notGrep, based on artifactId"() {
        setup:
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
                        dependency {
                            artifactId('artifact1NotFound')
                            version('1.0.0')
                        }
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "dependencies.dependency")

        when:
        XMLOperation otherXMLOperation = xmlOperation.notGrep {
            artifactId('artifact1NotFound')
        }

        then:
        otherXMLOperation.size() == 2
        otherXMLOperation.nodes[0].artifactId.text() == "artifact1"
        otherXMLOperation.nodes[1].artifactId.text() == "artifact2"
    }

    def "grep, artifactId not found based on multi criteria"() {
        setup:
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
        setup:
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

    def "grep, find by attribute"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"security", class:"MaSecurity"])
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            bean(id:"security")
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].@id == 'security'
    }

    def "grep, find by attribute, with two tag"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"transaction", class:"MaClass"]) {
                    property([agent:"MonAgent"])
                }
                bean([id:"security", class:"MaSecurity"])
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            bean(id:"security")
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].@id == 'security'
    }

    def "grep, find by attribute, with two tag and an inner tag criteria on attribute"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"transaction", class:"MaClass"]) {
                    property([agent:"MonAgent"])
                }
                bean([id:"security", class:"MaSecurity"])
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            bean(id:"transaction") {
                property([agent:"MonAgent"])
            }
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].@id == 'transaction'
    }

    def "grep, not find by attribute, with two tag and an inner tag criteria on attribute"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"transaction", class:"MaClass"]) {
                    property([agent:"MonAgent"])
                }
                bean([id:"security", class:"MaSecurity"])
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            bean(id:"transaction") {
                property([agent:"MyAgentNotExist"])
            }
        }

        then:
        otherXMLOperation.nodes.size() == 0
    }

    def "grep, find by attribute, with two tag and an inner tag criteria on value"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"transaction", class:"MaClass"]) {
                    property {
                        "test"
                    }
                }
                bean([id:"security", class:"MaSecurity"]) {
                    property {
                        "test"
                    }
                }
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.grep {
            bean(id:"transaction") {
                property {
                    "test"
                }
            }
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].@class == 'MaClass'
    }


    def "Multiple transformation on existing node"() {
        setup:
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

    def "Transformation on attribute"() {
        setup:
        Node node = buildNode({
            beans {
                bean([id:"id",class:"class"])
            }
        })
        XMLOperation xmlOperation = XMLOperation.create(node, "beans.bean")

        when:
        XMLOperation otherXMLOperation = xmlOperation.transform {
            bean([class:"otherClass"])
        }

        then:
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].'@class' == 'otherClass'
    }

    def "Multiple transformation on non existing node, nothing is transform"() {
        setup:
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
        setup:
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
        setup:
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
        setup:
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

    def "Append on spring xml file with namespace"() {
        setup:
        Node node = new XmlParser().parse(new File("./src/test/resources/xml/services-config.xml"))
        XMLOperation xmlOperation = XMLOperation.create(node, "beans")

        when:
        xmlOperation.append {
            mkp.declareNamespace(tx: "http://www.springframework.org/schema/tx")
            'tx:annotation-driven'('transaction-manager': 'transactionManager')
        }

        then:
        xmlOperation.nodes.'tx:annotation-driven' != null
        xmlOperation.nodes.'tx:annotation-driven'["@transaction-manager"][0] == "transactionManager"
    }
}
