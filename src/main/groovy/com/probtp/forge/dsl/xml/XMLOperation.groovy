package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.FileOperation

import static com.probtp.forge.dsl.xml.XMLUtils.hasSameName
import groovy.xml.StreamingMarkupBuilder

class XMLOperation implements FileOperation {

    private NodeList nodes
    private StreamingMarkupBuilder streamingMarkupBuilder = new StreamingMarkupBuilder()
    private XmlParser xmlParser = new XmlParser()

    NodeList getNodes() {
        return nodes
    }

    boolean isEmpty() {
        return nodes.isEmpty()
    }

    private Node buildNodeFrom(Closure closure) {
        return xmlParser.parseText(streamingMarkupBuilder.bind(closure).toString())
    }

    FileOperation append(Closure closure) {
        Node nodeToAppend = buildNodeFrom(closure)
        /*if(nodeToAppend.name() instanceof QName) {
            QName qName = (QName) nodeToAppend.name()
            println qName.getPrefix()
            Node nNode = new Node(null, qName.getPrefix() + ":" + qName.getLocalPart(), nodeToAppend.attributes())
            nodeToAppend.children().each {
                nNode.append(it)
            }
            nodeToAppend = nNode
        }*/

        nodes.each { Node node ->
            node.append(nodeToAppend)
        }

        return this
    }

    FileOperation removeAll() {
        if(nodes.isEmpty()) {
            return this
        }

        nodes.each { Node node ->
            node.parent().remove(node)
        }
        nodes = new NodeList()
        return this
    }

    FileOperation grep(Closure closure) {
        if(nodes.isEmpty()) {
            return this
        }
        Node criteriaParam = buildNodeFrom(closure)
        NodeList criterionTag
        if(!hasSameName(criteriaParam,nodes[0])) {
            criterionTag = new NodeList()
            criterionTag << criteriaParam
        } else {
            criterionTag = criteriaParam.children()
        }

        NodeList workingNodes = nodes
        criterionTag.each {Node nodeCriteria ->
            workingNodes = workingNodes.findAll { Node node ->
                def results = node.children().findAll {
                    hasSameName(it, nodeCriteria)
                }.findAll {
                    it.text() == nodeCriteria.text()
                }
                return !results.isEmpty()
            }
        }

        XMLOperation xmlOperation = new XMLOperation()
        xmlOperation.nodes = new NodeList(workingNodes)
        return xmlOperation
    }


    FileOperation transform(Closure closure) {
        if(nodes.isEmpty()) {
            return this
        }
        Node transformationParamNode = buildNodeFrom(closure)
        NodeList transformationParamNodes

        if(!hasSameName(transformationParamNode, nodes[0])) {
            transformationParamNodes = new NodeList()
            transformationParamNodes << transformationParamNode
        } else {
            transformationParamNodes = transformationParamNode.children()
        }

        nodes.each { Node nodeToTransform ->
            transformationParamNodes.each { Node transformationNode ->
                if(!nodeToTransform[transformationNode.name()].isEmpty()) {
                    nodeToTransform.remove(nodeToTransform[transformationNode.name()][0])
                }
                nodeToTransform.append(transformationNode)
            }
        }
        return this
    }

    static XMLOperation create(Node root, String path) {
        NodeList nodes = Path.create(root, path).get()
        if(nodes == null) {
            nodes = new NodeList()
        }
        return new XMLOperation(nodes:nodes)
    }

    static XMLOperation create(File file, String path) {
        XmlParser parser = new XmlParser()
        return create(parser.parse(file), path)
    }
}
