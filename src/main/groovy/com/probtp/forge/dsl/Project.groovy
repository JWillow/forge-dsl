package com.probtp.forge.dsl

import com.probtp.forge.dsl.yaml.YAMLFileHandler

import static com.probtp.forge.dsl.FileUtils.Extension.*
import com.probtp.forge.dsl.xml.XMLFileHandler

class Project {

    File rootDir

    List<FileHandler> fileHandlers = []

    FileHandler getAt(String pathExpression) {
        File file = new File(rootDir, pathExpression)
        FileHandler fileHandler
        switch (FileUtils.getExtension(file)) {
            case XML:
                fileHandler = XMLFileHandler.handle(file)
                break
            case YAML:
                fileHandler = YAMLFileHandler.handle(file)
                break
            default:
                return null

        }
        fileHandlers << fileHandler
        return fileHandler
    }

    void save() {
        fileHandlers.each {it.save()}
    }


}
