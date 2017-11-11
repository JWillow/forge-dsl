package com.probtp.forge.dsl.xml

import spock.lang.Specification

class XMLFileHandlerSpec extends Specification {
    def "save xml modification"() {
        setup:
        File toFile = new File("./target/ibm-web-bnd.xml")
        new AntBuilder().copy(file:"./src/test/resources/xml/ibm-web-bnd.xml",
                tofile:toFile)
        XMLFileHandler xmlFileHandler = XMLFileHandler.handle(toFile)
        XMLOperation xmlOperation = xmlFileHandler.'web-bnd'

        when:
        xmlOperation.append {
            tag('payload')
        }
        xmlFileHandler.save()

        then:
        XMLFileHandler otherXmlFileHandler = XMLFileHandler.handle(toFile)
        XMLOperation otherXMLOperation = otherXmlFileHandler.tag
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].text() == "payload"

        cleanup:
        toFile.deleteOnExit()
    }

    def "Generate path from missing Property method"() {
        setup:
        XMLFileHandler xmlFileHandler = XMLFileHandler.handle(new File("./src/test/resources/xml/jboss-web.xml"))

        when:
        XMLOperation operation = xmlFileHandler.'context-root'

        then:
        operation.nodes.size() == 1
        operation.nodes[0].text() == "remyContextRoot"
    }
}
