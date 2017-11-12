package com.probtp.forge.dsl.xml.utils

import groovy.xml.QName

class XMLUtils {

    static Node findParentByName(Node node, String parentName) {
        if (hasSameName(node, parentName)) {
            return node
        }
        if (node.parent() != null) {
            return findParentByName(node.parent(), parentName)
        }
        return null
    }

    static hasSameName(Object node, String name) {
        Object nodeName
        if (node instanceof Node) {
            nodeName = node.name()

            if (nodeName instanceof QName) {
                QName qn = (QName) nodeName
                return qn.matches(name)
            }
        } else {
            nodeName = node
        }
        return name == nodeName
    }

    static hasSameName(Node node, Node otherNode) {
        if (node == null) {
            return false
        }
        if (otherNode == null) {
            return false
        }
        Object nodeName = node.name()
        Object otherNodeName = otherNode.name()
        if (nodeName.getClass() == otherNodeName.getClass()) {
            return nodeName == (otherNodeName)
        }

        if (nodeName instanceof QName) {
            return ((QName) nodeName).matches(otherNodeName.toString())
        }
        return ((QName) otherNodeName).matches(nodeName.toString())
    }

    static Object value(Node node) {
        Object value
        if (node != null) {
            def nodeValue = node.value()
            if (nodeValue instanceof NodeList) {
                if (nodeValue.size() == 1
                        && !(nodeValue[0] instanceof NodeList)
                        && !(nodeValue[0] instanceof Node)) {
                    value =  nodeValue[0]
                }
            } else {
                value = node.value()
            }
        }
        return value
    }

    static NodeList children(Node node) {
        NodeList children = node.children()
        if (node.children().size() == 1 && !(node.children()[0] instanceof NodeList || node.children()[0] instanceof Node)) {
            return new NodeList()
        }
        return children
    }
}
