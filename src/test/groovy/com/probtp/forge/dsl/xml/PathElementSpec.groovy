package com.probtp.forge.dsl.xml

import spock.lang.Specification

class PathElementSpec extends Specification {

    def "Used optional pathElement"() {
        when:
            def pathElement = PathElement.fromRawPathElement("dependencies?")
        then:
            pathElement.isOptional()
            pathElement.get() == "dependencies"
    }

    def "Used no optional pathElement"() {
        when:
            def pathElement = PathElement.fromRawPathElement("dependencies")
        then:
            !pathElement.isOptional()
            pathElement.get() == "dependencies"
    }

    def "Just optional"() {
        when:
            PathElement.fromRawPathElement("?")
        then:
            thrown(IllegalArgumentException)
    }

    def "With null path"() {
        when:
            PathElement.fromRawPathElement(null)
        then:
            thrown(IllegalArgumentException)
    }
}
