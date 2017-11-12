package com.probtp.forge.dsl.xml

import com.probtp.forge.dsl.FileOperation
import com.probtp.forge.dsl.utils.FileUtils
import com.probtp.forge.dsl.xml.utils.AppenderUtils
import com.probtp.forge.dsl.xml.utils.XMLUtils
import groovy.text.SimpleTemplateEngine
import groovy.xml.StreamingMarkupBuilder

import static com.probtp.forge.dsl.utils.FileUtils.Extension.*
import static com.probtp.forge.dsl.utils.FileUtils.getExtension
import static com.probtp.forge.dsl.xml.utils.XMLUtils.*

class XMLOperation implements FileOperation {

    private NodeList nodes
    private Path path
    private StreamingMarkupBuilder streamingMarkupBuilder = new StreamingMarkupBuilder()
    private XmlParser xmlParser = new XmlParser()

    NodeList getNodes() {
        return nodes
    }

    int size() {
        return nodes.size()
    }

    boolean isEmpty() {
        return nodes.isEmpty()
    }

    private Node buildNodeFrom(Closure closure) {
        return xmlParser.parseText(streamingMarkupBuilder.bind(closure).toString())
    }

    FileOperation append(File file, Map<String, Object> parameters) {
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        String result = templateEngine.createTemplate(file).make(parameters)
        return append(getExtension(file), new ByteArrayInputStream(result.bytes))
    }

    FileOperation append(FileUtils.Extension extension, InputStream is) {
        switch (extension) {
            case XML:
                Node nodeToAppend = xmlParser.parse(is)
                nodes.each {Node node ->
                        node.append((Node) nodeToAppend.clone())
                }
                break
            case YAML:
                AppenderUtils.appendFromYAML(is, nodes)
                break
            case GROOVY:
                AppenderUtils.appendFromGroovy(is, nodes)
                break
            case PROPERTIES:
                AppenderUtils.appendFromProperties(is, nodes)
                break
        }
        return this
    }

    FileOperation append(File file) {
        return append(getExtension(file), new FileInputStream(file))
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

    /**
     * Use to produce a new instance of XMLOperation with a Path create from property value
     * @param property
     * @return new instance of XMLOperation
     */
    Object propertyMissing(String property) {
        // Property : translate to path
        Path path = Path.create(property)
        NodeList workingNodes = new NodeList()
        nodes.each { Node node ->
            workingNodes.addAll((Collection) path.get(node))
        }
        XMLOperation xmlOperation = new XMLOperation()
        xmlOperation.nodes = workingNodes
        xmlOperation.path = path
        return xmlOperation
    }

    static boolean hasAttribute(Node node, Node nodeCriteria) {
        return nodeCriteria.attributes().findAll { keyAttrCriteria, valueAttrCriteria ->
            valueAttrCriteria == node.attribute(keyAttrCriteria)
        }.size() == nodeCriteria.attributes().size()
    }

    static boolean grep(Node criteria, Node candidate) {
        if (!hasSameName(candidate, criteria)) {
            return false
        }
        if (!criteria.attributes().isEmpty()) {
            if (!hasAttribute(candidate, criteria)) {
                return false
            }
        }
        Object valueCriteria = value(criteria)
        if (valueCriteria) {
            if (value(candidate) != valueCriteria) {
                return false
            }
        }
        if (!children(criteria).isEmpty()) {
            if (children(candidate).isEmpty()) {
                return false
            }
            return criteria.children().findAll { Node childCriteria ->
                candidate.children().find { Node childCandidate ->
                    grep(childCriteria, childCandidate)
                }
            }.size() == criteria.children().size()
        }
        return true
    }

    /**
     * Search that doen't match the closure expression
     * @param closure - Node groovy structure as criteria
     * @return new instance of FileOperation with the subset of matching result
     */
    FileOperation notGrep(Closure closure) {
        FileOperation operation = grep(closure)
        def response = this.nodes - operation.nodes
        XMLOperation xmlOperation = new XMLOperation()
        xmlOperation.nodes = response
        xmlOperation.path = path
        return xmlOperation
    }

    /**
     * Search to grep by using closure as criteria
     * @param closure - Node groovy structure to gre
     * @return new instance of FileOperation with the subset of matching result
     */
    FileOperation grep(Closure closure) {
        if (nodes.isEmpty()) {
            return this
        }
        Node criteria = buildNodeFrom(closure)
        NodeList workingNodes = new NodeList()
        workingNodes.addAll(nodes)
        nodes.each { Node candidate ->
            workingNodes.addAll(candidate.depthFirst())
        }

        workingNodes = workingNodes.findAll { Object candidate ->
            if (candidate instanceof Node) {
                grep(criteria, candidate)
            }
        }

        // We keep only Parent corresponding to leaf path name
        String leafPathName = path.last().get()
        LinkedHashSet<Node> results = []
        workingNodes.each {
            Node result = XMLUtils.findParentByName(it, leafPathName)
            if (result) {
                results << result
            }
        }

        XMLOperation xmlOperation = new XMLOperation()
        xmlOperation.nodes = new NodeList(results)
        xmlOperation.path = path
        return xmlOperation
    }

    /**
     * Add or Merge Node
     * @param closure
     * @return
     */
    FileOperation transform(Closure closure) {
        if (nodes.isEmpty()) {
            return this
        }
        Node transformationParamNode = buildNodeFrom(closure)

        NodeList workingNodes = new NodeList(nodes)
        nodes.each { Node node ->
            workingNodes.addAll(node.depthFirst().findAll {it instanceof Node})
        }

        workingNodes.each { Node candidate ->
            if (!hasSameName(candidate, transformationParamNode)) {
                return
            }
            candidate.attributes().putAll(transformationParamNode.attributes())
            Node clone = transformationParamNode.clone()
            clone.children().each {
                if (it instanceof Node) {
                    NodeList candidateChildNodeToTransform = candidate[it.name()]
                    if (!candidateChildNodeToTransform.isEmpty()) {
                        candidateChildNodeToTransform.each { sub ->
                            if (sub instanceof Node) {
                                sub.attributes().putAll(it.attributes())
                                sub.setValue(it.value())
                            }
                        }
                    } else {
                        candidate.append(it)
                    }
                } else {
                    candidate.setValue(it)
                }
            }
        }
        return this
    }

    static XMLOperation create(Node root, String strPath) {
        Path path = Path.create(strPath)
        NodeList nodes = path.get(root)
        if (nodes == null) {
            nodes = new NodeList()
        }
        return new XMLOperation(nodes: nodes, path: path)
    }

}
