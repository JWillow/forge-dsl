package com.probtp.forge.dsl

import com.probtp.forge.dsl.properties.PropertiesFileHandler
import com.probtp.forge.dsl.utils.FileUtils
import com.probtp.forge.dsl.yaml.YAMLFileHandler

import static com.probtp.forge.dsl.utils.FileUtils.Extension.*
import com.probtp.forge.dsl.xml.XMLFileHandler

class Repository {

    File rootDir

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
            case PROPERTIES:
                fileHandler = PropertiesFileHandler.handle(file)
                break
            default:
                return new DefaultFileHandler(file:file)

        }
        return fileHandler
    }

}
