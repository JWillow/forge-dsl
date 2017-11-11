package com.probtp.forge.dsl.properties

import com.probtp.forge.dsl.FileHandler
import com.probtp.forge.dsl.xml.Path
import com.probtp.forge.dsl.xml.XMLOperation


class PropertiesFileHandler implements FileHandler {
    private File file
    Node node

    void save() {
        write(new PrintWriter(file))
    }

    Object propertyMissing(String path) {
        return XMLOperation.create(node, path)
    }

    void saveTo(Writer writer) {
        write(writer, node.children(), "")
    }

    private write(Writer writer, Object value, String path) {
        if (value instanceof NodeList) {
            value.each {
                if(it instanceof Node || it instanceof NodeList) {
                    String nPath
                    if(!path) {
                        nPath= it.name()
                    } else {
                        nPath = "${path}.${it.name()}"
                    }
                    write(writer, it, nPath)
                } else {
                    writer.println("$path=${it}")
                }
            }
            return
        }
        write(writer, value.children(), path)
    }

    static PropertiesFileHandler handle(Properties properties) {
        PropertiesFileHandler handler = new PropertiesFileHandler()
        Node root = new Node(null, "&root")
        properties.each { String key, String value ->
            NodeList nodes = Path.create(key, true).get(root)
            nodes.each { Node node ->
                node.setValue(value)
            }
        }
        handler.node = root
        return handler
    }

    static PropertiesFileHandler handle(File file) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(file))
        PropertiesFileHandler handler = handle(properties)
        handler.file = file
        return handler
    }
}
