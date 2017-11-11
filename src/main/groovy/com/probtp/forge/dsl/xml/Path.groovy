package com.probtp.forge.dsl.xml

import static com.probtp.forge.dsl.xml.XMLUtils.hasSameName
import groovy.transform.ToString

@ToString(includeNames=true,includeFields=true)
class Path {
    private List<PathElement> pathElements
    private Path() {}

    PathElement last() {
        if(pathElements.isEmpty()) {
            return null
        }
        return pathElements[pathElements.size() - 1]
    }

    private NodeList _find(NodeList workingNodes, int pathElementIndex, boolean optionalModeEnable) {
        PathElement pathElement = pathElements[pathElementIndex]
        if(!optionalModeEnable) {
            optionalModeEnable = pathElement.isOptional()
        }
        NodeList results = new NodeList(workingNodes.findResults{
            NodeList nodes = (NodeList) it[pathElement.get()]
            return !nodes.isEmpty() ? nodes : null
        }).flatten()
        if(results.isEmpty()) {
            if(optionalModeEnable) {
                results = new NodeList(workingNodes.collect { it.appendNode(pathElement.get())})
            } else {
                return new NodeList()
            }
        }

        if(pathElementIndex == pathElements.size() -1) {
            return results
        }

        return _find(results, ++pathElementIndex, optionalModeEnable)
    }

    NodeList get(Node root) {
        NodeList workingNodes
        if (hasSameName(root, pathElements[0].get())) {
            workingNodes = new NodeList([root])
        } else {
            workingNodes = root.depthFirst().findAll { Object node ->
                hasSameName(node, pathElements[0].get())
            }
        }

        if (workingNodes.isEmpty()) {
            if (pathElements[0].isOptional()) {
                workingNodes = new NodeList([root.appendNode(pathElements[0].get())])
            } else {
                return new NodeList()
            }
        }

        if(pathElements.size() > 1) {
            return _find(workingNodes, 1, pathElements[0].isOptional())
        }
        return workingNodes
    }

    static Path create(String path) {
        def pathElements = []
        StringBuffer  word = new StringBuffer()
        boolean quoteEnable = false
        path.each {
            if(it == "." && !quoteEnable) {
                pathElements << PathElement.fromRawPathElement(word.toString())
                word = new StringBuffer()
            } else if(it == "'") {
                quoteEnable = !quoteEnable
            } else {
                word.append it
            }
        }
        pathElements << PathElement.fromRawPathElement(word.toString())
        return new Path(pathElements:pathElements)
    }
}
