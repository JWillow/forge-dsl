package com.probtp.forge.dsl.xml.utils

import com.probtp.forge.dsl.utils.ConvertUtils
import com.probtp.forge.dsl.xml.utils.XMLUtils
import org.yaml.snakeyaml.Yaml

class AppenderUtils {

    static appendFromYAML(InputStream is, NodeList nodes) {
        Yaml parser = new Yaml()
        Node yamlNodeToAppend = ConvertUtils.convert(parser.load(is))
        append(nodes, yamlNodeToAppend)
    }


    static appendFromGroovy(InputStream is, NodeList nodes) {
        XmlParser xmlParser = new XmlParser()
        Node nodeToAppend = xmlParser.parse(is)
        append(nodes, nodeToAppend)
    }

    static appendFromProperties(InputStream is, NodeList nodes) {
        Properties properties = new Properties()
        properties.load(is)
        Node nodeToAppend = ConvertUtils.convert(properties)
        append(nodes, nodeToAppend)
    }

    static private append(NodeList nodes, Node nodeToAppend) {
        boolean ignoreRoot = XMLUtils.hasSameName(nodeToAppend, "&root")
        nodes.each { Node node ->
            if (ignoreRoot) {
                // We must ignore the root node of YAML/Properties because it is artificial
                nodeToAppend.children().each { yamlChildNodeToAppend ->
                    node.append(yamlChildNodeToAppend.clone())
                }
            } else {
                node.append(nodeToAppend.clone())
            }
        }
    }
}
