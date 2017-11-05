package com.probtp.forge.dsl

import spock.lang.Specification

class ConvertUtilSpec extends Specification {

    def "simple structure - one root node"() {
        when:
        Node node = ConvertUtil.convert([test:"value"])

        then:
        node != null
        node.test != null
        node.text() == "value"
    }

    def "Simple structure - two root node"() {
        when:
        Node node = ConvertUtil.convert([test:"value", otherTest:"otherValue"])

        then:
        node != null
        node.'&root' != null
        node.children().size() == 2
        node.test[0].text() == "value"
        node.otherTest[0].text() == "otherValue"

    }

    def "simple structure with enum - one root node"() {
        when:
        Node node = ConvertUtil.convert([test:["value1", "value2"]])

        then:
        node != null
        node.children().size() == 2
        node."&sequence"[0].text() == "value1"
        node."&sequence"[1].text() == "value2"
    }

    def "simple structure with map value - one root node"() {
        when:
        Node node = ConvertUtil.convert([test:[subtest:["value1", "value2"]]])

        then:
        node != null
        node.children().size() == 1
        node.subtest[0].children().size() == 2
        node.subtest[0]."&sequence"[0].text() == "value1"
        node.subtest[0]."&sequence"[1].text() == "value2"
    }
}
