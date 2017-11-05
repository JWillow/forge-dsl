package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.FileHandler

class XMLFileHandler implements FileHandler {

    private Node node
    private File file

    private XMLFileHandler() {}

    void save() {
        PrintWriter printWriter = new PrintWriter(file)
        printWriter.println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        XmlNodePrinter nodePrinter = new XmlNodePrinter(printWriter)
        nodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            namespaceAware = true
            quote = "'" // Use single quote for attributes
        }
        nodePrinter.print(node)
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
