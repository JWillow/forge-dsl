package com.probtp.forge.dsl

import com.probtp.forge.dsl.xml.XMLFileHandler

class Project {

    File rootDir

    List<FileHandler> fileHandlers = []

    FileHandler getAt(String pathExpression) {
        File file = new File(rootDir, pathExpression)
        String[] tokens = file.toPath().getFileName().toString().split("\\.")
        if(tokens.size() != 2) {
            throw new IllegalArgumentException()
        }
        FileHandler fileHandler
        switch (tokens[1]) {
            case "xml":
                fileHandler = XMLFileHandler.handle(file)
            default:
                return null

        }
        fileHandlers << fileHandler
    }

    void save() {
        fileHandlers.each {it.save()}
    }


}
