package com.probtp.forge.dsl

import spock.lang.Specification

class RepositorySpec extends Specification {

    def "Project xml file handler - simple syntax"() {
        setup:
        Repository repository = new Repository(rootDir: new File("."))

        when:
        def xmlOperation = repository['pom.xml'].name
        then:
        xmlOperation != null
        xmlOperation.size() == 1
        xmlOperation.nodes[0].text() == "forge-dsl"
    }

    def "Project xml file handler - complex path syntax"() {
        setup:
        Repository project = new Repository(rootDir: new File("."))

        when:
        def xmlOperation = project['pom.xml']."properties.'project.build.sourceEncoding'"

        then:
        xmlOperation != null
        xmlOperation.nodes.size() == 1
        xmlOperation.nodes[0].text() == "UTF-8"
    }
}
