package com.probtp.forge.dsl.xml

import groovy.xml.QName

class XMLUtils {

    static hasSameName(Node node, String name) {
        Object nodeName = node.name();
        if (nodeName instanceof QName) {
            QName qn = (QName) nodeName;
            return qn.matches(name)
        }
        return name == nodeName
    }

    static hasSameName(Node node, Node otherNode) {
        if(node == null) {
            return false
        }
        if(otherNode == null) {
            return false
        }
        Object nodeName = node.name()
        Object otherNodeName = otherNode.name()
        if(nodeName.getClass() == otherNodeName.getClass()) {
            return nodeName == (otherNodeName)
        }

        if(nodeName instanceof QName) {
            return ((QName) nodeName).matches(otherNodeName.toString())
        }
        return ((QName) otherNodeName).matches(nodeName.toString())
    }
}