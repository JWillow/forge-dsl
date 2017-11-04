package com.probtp.forge.dsl.xml

import groovy.transform.ToString

@ToString(includeNames=true,includeFields=true)
class PathElement {
    private String element
    private boolean optional

    private PathElement() {}

    boolean isOptional() {
        return optional
    }

    String get() {
        return element
    }

    static PathElement fromRawPathElement(String raw) {
        if(raw == null || raw == "?") {
            throw new IllegalArgumentException()
        }
        boolean optional = raw.endsWith("?")
        String element = raw
        if(optional) {
            element = raw [0..(raw.size()-2)]
        }
        return new PathElement(element:element,optional:optional)
    }
}
