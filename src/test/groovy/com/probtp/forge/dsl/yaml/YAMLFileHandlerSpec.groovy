package com.probtp.forge.dsl.yaml

import spock.lang.Specification

class YAMLFileHandlerSpec extends Specification {

    def "simple test"() {
        when:
        YAMLFileHandler handler = YAMLFileHandler.handle("src/test/resources/yaml/simple.yml" as File)

        then:
        handler.number.nodes.size() == 6

    }

    def "test complex structure"() {
        when:
        YAMLFileHandler handler = YAMLFileHandler.handle("src/test/resources/yaml/simple.yml" as File)

        then:
        handler.'product.&sequence.sku'.nodes.size() == 2
    }

    def "test simple enum"() {
        when:
        YAMLFileHandler handler = YAMLFileHandler.handle("""
           ingredients: ['riz', 'vinaigre', 'sucre', 'sel', 'thon', 'saumon'] 
        """)

        then:
        handler.'&sequence'.nodes.size() == 6
    }

    def "test complex enum"() {
        when:
        YAMLFileHandler handler = YAMLFileHandler.handle("""
            product:
                - sku         : BL394D
                  quantity    : 4
                  description : Basketball
                  price       : 450.00
                - sku         : BL4438H
                  quantity    : 1
                  description : Super Hoop
                  price       : 2392.00
        """)

        then:
        handler.'&sequence'.nodes.size() == 2
        handler.'&sequence'.nodes[0].sku.text() == "BL394D"
        handler.'&sequence'.nodes[0].quantity.text() == "4"
        handler.'&sequence'.nodes[1].sku.text() == "BL4438H"
        handler.'&sequence'.nodes[1].quantity.text() == "1"
    }

    def "test complex enum to YAML"() {
        setup:
        YAMLFileHandler handler = YAMLFileHandler.handle("""
            product:
                - sku         : BL394D
                  quantity    : 4
                  description : Basketball
                  price       : 450.00
                - sku         : BL4438H
                  quantity    : 1
                  description : Super Hoop
                  price       : 2392.00
        """)

        when:
        StringWriter writer = new StringWriter()
        handler.saveTo(writer)

        then:
        def result = writer.toString()
        result == "product:\n" +
                "- sku: BL394D\n" +
                "  quantity: 4\n" +
                "  description: Basketball\n" +
                "  price: 450.0\n" +
                "- sku: BL4438H\n" +
                "  quantity: 1\n" +
                "  description: Super Hoop\n" +
                "  price: 2392.0\n"
    }

    def "test save simple enum"() {
        setup:
        YAMLFileHandler handler = YAMLFileHandler.handle("""
           ingredients: ['riz', 'vinaigre', 'sucre', 'sel', 'thon', 'saumon'] 
        """)

        when:
        StringWriter writer = new StringWriter()
        handler.saveTo(writer)
        String output = writer.toString()

        then:
        output == "ingredients:\n" +
                "- riz\n" +
                "- vinaigre\n" +
                "- sucre\n" +
                "- sel\n" +
                "- thon\n" +
                "- saumon\n"
    }

    def "simple save test"() {
        setup:
        YAMLFileHandler handler = YAMLFileHandler.handle("src/test/resources/yaml/simple.yml" as File)

        when:
        StringWriter writer = new StringWriter()
        handler.saveTo(writer)
        String output = writer.toString()

        then:
        println output
    }
}
