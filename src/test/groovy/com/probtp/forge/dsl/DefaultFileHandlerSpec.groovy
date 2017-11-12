package com.probtp.forge.dsl


import spock.lang.Specification

class DefaultFileHandlerSpec extends Specification {


    static DefaultFileHandler createDefaultFileHandlerFileWith(String name, String data) {
        File file = new File(name)
        file << data
        assert file.exists()
        return DefaultFileHandler.handle(file)
    }

    def "Copy file"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")
        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        when:
        handler >> new File("./target")

        then:
        targetedFile.exists()

        cleanup:
        targetedFile.delete()
        handler.delete()
    }

    def "Copy file, the FileHandler way"() {
        setup:
        DefaultFileHandler srcHandler =  createDefaultFileHandlerFileWith("./test.txt", "Test")

        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        DefaultFileHandler destFileHandler = new DefaultFileHandler(file: new File("./target"))

        when:
        srcHandler >> destFileHandler

        then:
        targetedFile.exists()

        cleanup:
        srcHandler.delete()
        targetedFile.delete()
    }

    def "Copy file, the String way"() {
        setup:
        DefaultFileHandler srcHandler = createDefaultFileHandlerFileWith("./test.txt", "Test")

        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        when:
        srcHandler >> "./target"

        then:
        targetedFile.exists()

        cleanup:
        srcHandler.delete()
        targetedFile.delete()
    }

    def "Delete file by -- way"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")

        when:
        handler --

        then:
        !handler.file.exists()
    }

    def "Delete file by delete way"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")

        when:
        handler.delete()

        then:
        !handler.file.exists()
    }

    def "Move file"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")
        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        when:
        handler >>> new File("./target")

        then:
        targetedFile.exists()
        !handler.file.exists()

        cleanup:
        targetedFile.delete()
    }

    def "Move file, handler way"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")
        DefaultFileHandler destHandler = DefaultFileHandler.handle(new File("./target"))
        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        when:
        handler >>> destHandler

        then:
        targetedFile.exists()
        !handler.file.exists()

        cleanup:
        targetedFile.delete()
    }

    def "Move file, string way"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", "Test")
        File targetedFile = new File("./target/test.txt")
        assert !targetedFile.exists()

        when:
        handler >>> "./target"

        then:
        targetedFile.exists()
        !handler.file.exists()

        cleanup:
        targetedFile.delete()
    }

    def "Template file"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", 'Hello ${name}')

        when:
        TemplateResultValue templateResultValue = (handler >> [name:"Remy"])

        then:
        templateResultValue.value == "Hello Remy"

        cleanup:
        handler.delete()
    }

    def "Template file usage with String destination"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", 'Hello ${name}')
        File targetedFile = new File("./target/test.txt")

        when:
        handler >> [name:"Remy"] >> "./target/test.txt"

        then:
        targetedFile.getText() == "Hello Remy"

        cleanup:
        handler.delete()
        targetedFile.delete()
    }

    def "Template file usage with File destination"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", 'Hello ${name}')
        File targetedFile = new File("./target/test.txt")

        when:
        handler >> [name:"Remy"] >> targetedFile

        then:
        targetedFile.getText() == "Hello Remy"

        cleanup:
        handler.delete()
        targetedFile.delete()
    }

    def "Template file usage with handler destination"() {
        setup:
        DefaultFileHandler handler = createDefaultFileHandlerFileWith("./test.txt", 'Hello ${name}')
        File targetedFile = new File("./target/test.txt")
        DefaultFileHandler destHandler = DefaultFileHandler.handle(targetedFile)

        when:
        handler >> [name:"Remy"] >> destHandler

        then:
        targetedFile.getText() == "Hello Remy"

        cleanup:
        handler.delete()
        targetedFile.delete()
    }


}
