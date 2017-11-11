package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.ConvertUtil
import org.yaml.snakeyaml.Yaml

class AppenderUtils {

    static appendFromYAML(InputStream is, NodeList nodes) {
        Yaml parser = new Yaml()
        Node yamlNodeToAppend = ConvertUtil.convert(parser.load(is))
        append(nodes, yamlNodeToAppend)
    }

    static private append(NodeList nodes, Node nodeToAppend) {
        boolean ignoreRoot = XMLUtils.hasSameName(nodeToAppend, "&root")
        nodes.each { Node node ->
            if (ignoreRoot) {
                // We must ignore the root node of YAML because it is artificial
                yamlNodeToAppend.children().each { yamlChildNodeToAppend ->
                    node.append(yamlChildNodeToAppend.clone())
                }
            } else {
                node.append(nodeToAppend.clone())
            }
        }
    }
    static appendFromGroovy(InputStream is, NodeList nodes) {
        XmlParser xmlParser = new XmlParser()
        Node nodeToAppend = xmlParser.parse(is)
        append(nodes, nodeToAppend)
    }
}
