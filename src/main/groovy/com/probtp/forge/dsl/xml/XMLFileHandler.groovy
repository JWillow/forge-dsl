package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.FileHandler

class XMLFileHandler implements FileHandler {

    private Node node
    private File file

    private XMLFileHandler() {}

    void saveTo(Writer writer) {
        writer.println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(writer))
        nodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            namespaceAware = true
            quote = "'" // Use single quote for attributes
        }
        nodePrinter.print(node)
    }

    void save() {
        saveTo(new PrintWriter(file))
    }

    @Override
    Object getProperty(String property) {
        return XMLOperation.create(node, property)
    }

    static XMLFileHandler handle(File file) {
        XMLFileHandler handler = new XMLFileHandler()
        handler.node = new XmlParser().parse(file)
        handler.file = file
        return handler
    }
}
