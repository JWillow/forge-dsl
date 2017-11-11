package com.probtp.forge.dsl

import com.probtp.forge.dsl.xml.XMLOperation
import spock.lang.Specification

class DSLSpec extends Specification {

    def "Remove the scope only for dependencies with test value"() {
        setup:
        Project project = new Project(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep{scope("test")}.size() == 7
        assert xmlOperationDependencyTag.grep{scope("compile")}.size() == 1

        when:
        xmlOperationDependencyTag.grep{scope("test")}.scope.removeAll()

        then:
        xmlOperationDependencyTag.grep{scope("test")}.size() == 0
        xmlOperationDependencyTag.grep{scope("compile")}.size() == 1

        /*StringWriter stringWriter = new StringWriter()
        project.fileHandlers[0].saveTo(stringWriter)
        println stringWriter*/
    }

    def "Transform all test scope to compile scope"() {
        setup:
        Project project = new Project(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1

        when:
        xmlOperationDependencyTag.grep { scope("test") }.scope.transform { scope('compile')}

        then:
        xmlOperationDependencyTag.grep { scope("test") }.size() == 0
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 8
    }

    def "Transform all test scope to compile scope, other method"() {
        setup:
        Project project = new Project(rootDir: new File("."))
        XMLOperation xmlOperationDependencyTag = project['pom.xml'].dependency
        assert xmlOperationDependencyTag.grep { scope("test") }.size() == 7
        assert xmlOperationDependencyTag.grep { scope("compile") }.size() == 1

        when:
        xmlOperationDependencyTag.grep { scope("test") }.transform { scope('compile')}

        then:
        xmlOperationDependencyTag.grep { scope("test") }.size() == 0
        xmlOperationDependencyTag.grep { scope("compile") }.size() == 8
    }
}
