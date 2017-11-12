package com.probtp.forge.dsl.utils

import com.probtp.forge.dsl.xml.Path

class ConvertUtils {

    static void _convert(Node root, Map<String, Object> structure) {
        structure.each { key, value ->
            Node node = new Node(root, key)
            _convert(node, value)
        }
    }

    static void _convert(Node root, List<Object> objects) {
        objects.each {
            Node node = new Node(root, "&sequence")
            if (it instanceof Map) {
                _convert(node, it)
            } else {
                node.setValue(it)
            }
        }
    }

    static void _convert(Node root, Object value) {
        root.setValue(value)
    }

    static Node convert(Map<String, Object> structure) {
        Node node = null
        if (structure.keySet().size() > 1) {
            node = new Node(null, "&root")
            structure.each { key, value ->
                Node subNode = new Node(node, key)
                _convert(subNode, value)
            }
        } else {
            Map.Entry<String, Object> entry = structure.entrySet()[0];
            node = new Node(null, entry.key)
            _convert(node, entry.value)
        }
        return node
    }

    static void _convertFromNode(Node node, List<Object> sequence) {
        if (!(node.value() instanceof NodeList)) {
            sequence << node.value()
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>()
            node.children().each { Node child ->
                _convertFromNode(child, map)
            }
            sequence << map
        }
    }

    static void _convertFromNode(Node node, LinkedHashMap<String, Object> map) {
        if (!(node.value() instanceof NodeList)) {
            map.put(node.name().toString(), node.value())
        } else {
            LinkedHashMap<String, Object> subMap = new LinkedHashMap<String, Object>()
            ArrayList<Object> sequence = new ArrayList<Object>()
            node.children().each { Object child ->
                if(child instanceof Node) {
                    if(child.name() == "&sequence") {
                        _convertFromNode(child, sequence)
                    } else {
                        _convertFromNode(child, subMap)
                    }
                } else {
                    sequence << child
                }
            }
            if (!subMap.isEmpty()) {
                map.put(node.name().toString(), subMap)
            } else {
                map.put(node.name().toString(), sequence)
            }
        }
    }

    static Map<String, Object> convertFromNode(Node node) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>()
        if (node.name() == "&root") {
            node.children().each { Node child ->
                _convertFromNode(child, maps)
            }
        } else {
            _convertFromNode(node, maps)
        }
        return maps
    }


    static Node convert(Properties properties) {
        Node root = new Node(null, "&root")
        properties.each { String key, String value ->
            NodeList nodes = Path.create(key, true).get(root)
            nodes.each { Node node ->
                node.attributes().put("value",value)
            }
        }
        return root
    }
}
