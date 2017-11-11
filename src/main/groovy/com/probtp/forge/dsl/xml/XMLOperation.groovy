package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.FileOperation
import groovy.text.SimpleTemplateEngine
import groovy.xml.StreamingMarkupBuilder

import static com.probtp.forge.dsl.xml.XMLUtils.*

class XMLOperation implements FileOperation {

    private NodeList nodes
    private Path path
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

    FileOperation append(File fileName, Map<String, Object> parameters) {
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        String result = templateEngine.createTemplate(fileName).make(parameters)
        Node nodeToAppend = xmlParser.parseText(result)
        nodes.each { Node node ->
            node.append(nodeToAppend)
        }
        return this
    }

    FileOperation append(File fileName) {
        Node nodeToAppend = xmlParser.parse(fileName)
        nodes.each { Node node ->
            node.append(nodeToAppend)
        }
        return this
    }

    FileOperation append(Closure closure) {
        Node nodeToAppend = buildNodeFrom(closure)
        nodes.each { Node node ->
            node.append(nodeToAppend)
        }
        return this
    }

    FileOperation removeAll() {
        if (nodes.isEmpty()) {
            return this
        }
        nodes.each { Node node ->
            node.parent().remove(node)
        }
        nodes = new NodeList()
        return this
    }

    boolean hasAttribute(Node node, Node nodeCriteria) {
        return nodeCriteria.attributes().findAll { keyAttrCriteria, valueAttrCriteria ->
            valueAttrCriteria == node.attribute(keyAttrCriteria)
        }.size() == nodeCriteria.attributes().size()
    }

    boolean grep(Node criteria, Node candidate) {
        if(!hasSameName(candidate, criteria)) {
            return false
        }
        if(!criteria.attributes().isEmpty()) {
            if(!hasAttribute(candidate, criteria)) {
                return false
            }
        }
        Object valueCriteria = value(criteria)
        if(valueCriteria) {
            if(value(candidate) != valueCriteria) {
                return false
            }
        }
        if(!children(criteria).isEmpty()) {
            if(children(candidate).isEmpty()) {
                return false
            }
            return criteria.children().findAll {Node childCriteria ->
                candidate.children().find { Node childCandidate ->
                    grep(childCriteria, childCandidate)
                }
            }.size() == criteria.children().size()
        }
        return true
    }

    FileOperation grep(Closure closure) {
        if (nodes.isEmpty()) {
            return this
        }
        Node criteria =  buildNodeFrom(closure)
        NodeList workingNodes = new NodeList()
        workingNodes.addAll(nodes)
        nodes.each {Node candidate ->
            workingNodes.addAll(candidate.depthFirst())
        }

        workingNodes = workingNodes.findAll {Node candidate ->
            grep(criteria, candidate)
        }

        // We keep only Parent corresponding to leaf path name
        String leafPathName = path.last().get()
        LinkedHashSet<Node> results = []
        workingNodes.each {
            Node result = XMLUtils.findParentByName(it, leafPathName)
            if(result) {
                results << result
            }
        }

        XMLOperation xmlOperation = new XMLOperation()
        xmlOperation.nodes = new NodeList(results)
        xmlOperation.path = path
        return xmlOperation
    }


    FileOperation transform(Closure closure) {
        if (nodes.isEmpty()) {
            return this
        }
        Node transformationParamNode = buildNodeFrom(closure)
        NodeList transformationParamNodes

        if (!hasSameName(transformationParamNode, nodes[0])) {
            transformationParamNodes = new NodeList()
            transformationParamNodes << transformationParamNode
        } else {
            transformationParamNodes = transformationParamNode.children()
        }

        nodes.each { Node nodeToTransform ->
            transformationParamNodes.each { Node transformationNode ->
                if (!nodeToTransform[transformationNode.name()].isEmpty()) {
                    nodeToTransform.remove(nodeToTransform[transformationNode.name()][0])
                }
                nodeToTransform.append(transformationNode)
            }
        }
        return this
    }

    static XMLOperation create(Node root, String strPath) {
        Path path = Path.create(root, strPath)
        NodeList nodes = path.get()
        if (nodes == null) {
            nodes = new NodeList()
        }

        return new XMLOperation(nodes: nodes, path:path)
    }

}
