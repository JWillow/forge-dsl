package com.probtp.forge.dsl

class ConvertUtil {

    static void _convert(Node root, Map<String, Object> structure) {
        structure.each {key, value ->
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

    static Map<String, Object> convert(Node node) {
        //TODO
    }
    static Node convert(Map<String, Object> structure) {
        Node node = null
        if(structure.keySet().size() > 1) {
            node = new Node(null,"&root")
            structure.each {key, value ->
                Node subNode = new Node(node, key)
                _convert(subNode, value)
            }
        } else {
            Map.Entry<String,Object> entry = structure.entrySet()[0];
            node = new Node(null,entry.key)
            _convert(node, entry.value)
        }
        return node
    }
}
