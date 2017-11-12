package com.probtp.forge.dsl.properties

import com.probtp.forge.dsl.FileOperation
import spock.lang.Specification

class PropertiesFileHandlerSpec extends Specification {

    def "Handle simple properties file- read and write"() {
        setup:
        StringWriter writer = new StringWriter()
        File file = new File("./file.properties")
        file << "application.db.url=apache.org\n"
        file << "application.db.password=commanche\n"

        when:
        PropertiesFileHandler handler = PropertiesFileHandler.handle(file)
        handler.saveTo(writer)

        then:
        writer.toString() == "application.db.password=commanche\n" +
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
        operation.size() == 1
        operation.nodes[0].'@value' == "org.apache.log4j.ConsoleAppender"
        operation.nodes.size() == 1
    }

    def "Append properties file"() {
        setup:
        StringWriter writer = new StringWriter()
        File file = new File("./file.properties")
        file << "application.db.url=apache.org\n"
        file << "application.db.password=commanche\n"
        File fileToAppend = new File("./src/test/resources/properties/file.properties")
        PropertiesFileHandler handler = PropertiesFileHandler.handle(file)

        when:
        handler.'&root'.append fileToAppend
        handler.saveTo(writer)

        then:
        writer.toString() == "application.db.password=commanche\n" +
                "application.db.url=apache.org\n" +
                "log4j.rootLogger=DEBUG, stdout\n" +
                "log4j.appender.stdout=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.stdout.layout.ConversionPattern=%d [%-5p] (%F:%M:%L) %m%n\n"

        cleanup:
        file.deleteOnExit()
    }
}
