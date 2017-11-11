package com.probtp.forge.dsl

import spock.lang.Specification

class ProjectSpec extends Specification {

    def "Project xml file handler - simple syntax"() {
        setup:
        Project project = new Project(rootDir: new File("."))

        when:
        def xmlOperation = project['pom.xml'].name
        then:
        xmlOperation != null
        xmlOperation.nodes.size() == 1
        xmlOperation.nodes[0].text() == "forge-dsl"
    }

    def "Project xml file handler - complex path syntax"() {
        setup:
        Project project = new Project(rootDir: new File("."))

        when:
        def xmlOperation = project['pom.xml']."properties.'project.build.sourceEncoding'"

        then:
        xmlOperation != null
        xmlOperation.nodes.size() == 1
        xmlOperation.nodes[0].text() == "UTF-8"
    }
}
