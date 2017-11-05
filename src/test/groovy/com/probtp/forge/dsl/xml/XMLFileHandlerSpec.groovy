package com.probtp.forge.dsl.xml

import spock.lang.Specification

class XMLFileHandlerSpec extends Specification {
    def "save xml modification"() {
        setup:
        String workingFileName = "ibm-web-bnd.xml"
        new AntBuilder().copy(file:"./src/test/resources/ibm-web-bnd.xml",
                tofile:"./target/$workingFileName")
        XMLFileHandler xmlFileHandler = XMLFileHandler.handle(new File("./target/$workingFileName"))
        XMLOperation xmlOperation = xmlFileHandler.'web-bnd'

        when:
        xmlOperation.append {
            tag('payload')
        }
        xmlFileHandler.save()

        then:
        XMLFileHandler otherXmlFileHandler = XMLFileHandler.handle(new File("./target/$workingFileName"))
        XMLOperation otherXMLOperation = otherXmlFileHandler.tag
        otherXMLOperation.nodes.size() == 1
        otherXMLOperation.nodes[0].text() == "payload"
    }

    def "Generate path from missing Property method"() {
        setup:
        XMLFileHandler xmlFileHandler = XMLFileHandler.handle(new File("./src/test/resources/jboss-web.xml"))

        when:
        XMLOperation operation = xmlFileHandler.'context-root'

        then:
        operation.nodes.size() == 1
        operation.nodes[0].text() == "remyContextRoot"
    }
}
