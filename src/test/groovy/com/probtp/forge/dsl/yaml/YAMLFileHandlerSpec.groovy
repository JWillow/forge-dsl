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
}
