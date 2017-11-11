package com.probtp.forge.dsl.properties

import com.probtp.forge.dsl.FileOperation
import spock.lang.Specification

class PropertiesFileHandlerSpec extends Specification {

    def "Handle simple properties file- read and write"() {
        setup:
        StringWriter writer = new StringWriter()
        File file = new File("./file.properties")
        file << "application.db.url=apache.org\n"
        file << "application.db.password=comanche\n"

        when:
        PropertiesFileHandler handler = PropertiesFileHandler.handle(file)
        handler.saveTo(writer)

        then:
        writer.toString() == "application.db.password=comanche\n" +
                "application.db.url=apache.org\n"

        cleanup:
        file.deleteOnExit()
    }

    def "Handle simple properties file- read and write in file"() {
        setup:
        StringWriter writer = new StringWriter()
        File file = new File("./src/test/resources/properties/file.properties")

        when:
        FileOperation operation = PropertiesFileHandler.handle(file)."appender.stdout"

        then:
        operation.size() == 3
    }
}
