package com.probtp.forge.dsl


class FileUtils {

    static enum Extension {XML, YAML, PROPERTIES, UNKNOWN, JSON, GROOVY}

    static Extension getExtension(File file) {
        String[] tokens = file.toPath().getFileName().toString().split("\\.")
        if(tokens.size() != 2) {
            throw new IllegalArgumentException()
        }
        switch (tokens[1]) {
            case "xml" :
                return Extension.XML
            case "yaml" :
                return Extension.YAML
            case "properties" :
                return Extension.PROPERTIES
            case "json" :
                return Extension.JSON
            case "groovy" :
                return Extension.JSON
            default:
                return Extension.UNKNOWN
        }
    }

}
