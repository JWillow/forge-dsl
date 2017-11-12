package com.probtp.forge.dsl

import com.probtp.forge.dsl.xml.XMLOperation
import spock.lang.Specification

class POMSample extends Specification {

    def "Remove the scope only for dependencies with test value"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1

        when:
        xmlOperationDependencyTag.grep { scope("test") }.scope.removeAll()

        then:
        xmlOperationDependencyTag.grep { scope("test") }.size() == 0
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 1
    }

    def "Transform all test scope to compile scope"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1

        when:
        xmlOperationDependencyTag.grep { scope("test") }.scope.transform { scope('compile') }

        then:
        xmlOperationDependencyTag.grep { scope("test") }.size() == 0
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 8
    }

    def "Transform all test scope to compile scope, other method"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1

        when:
        xmlOperationDependencyTag.grep { scope("test") }.transform { scope('compile') }

        then:
        xmlOperationDependencyTag.grep { scope("test") }.size() == 0
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 8
    }

    def "Add compile scope to all none scoped dependencies"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.size() == 10
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1
        assert xmlOperationDependencyTag.notGrep { scope() }.size() == 2

        when:
        xmlOperationDependencyTag.notGrep { scope() }.append { scope("compile") }

        then:
        xmlOperationDependencyTag.size() == 10
        xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 3
        xmlOperationDependencyTag.notGrep { scope() }.size() == 0
    }

    def "Change version of snakeyaml"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))

        when:
        FileOperation operation = project['pom.xml'].dependency.grep { artifactId("snakeyaml") }.transform {
            version("2.0")
        }

        then:
        operation.size() == 1
        operation.nodes[0].artifactId[0].text() == "snakeyaml"
        operation.nodes[0].version.text() == "2.0"
    }
}
