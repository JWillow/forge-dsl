package com.probtp.forge.dsl.properties

import com.probtp.forge.dsl.DefaultFileHandler
import com.probtp.forge.dsl.utils.ConvertUtils
import com.probtp.forge.dsl.FileHandler
import com.probtp.forge.dsl.xml.XMLOperation


class PropertiesFileHandler extends DefaultFileHandler implements FileHandler {
    private File file
    Node node

    File getFile() {
        return file
    }

    void save() {
        write(new PrintWriter(file))
    }

    Object propertyMissing(String path) {
        return XMLOperation.create(node, path)
    }

    void saveTo(Writer writer) {
        write(writer, node.children(), null)
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
                    if(it instanceof Node) {
                        Object attrValue = it.attribute("value")
                        if(attrValue) {
                            writer.println("$nPath=${attrValue}")
                        }
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

    static PropertiesFileHandler handle(File file) {
        Properties properties = new Properties()
        properties.load(new FileInputStream(file))
        PropertiesFileHandler handler = new PropertiesFileHandler()
        handler.node = ConvertUtils.convert(properties)
        handler.file = file
        return handler
    }
}
