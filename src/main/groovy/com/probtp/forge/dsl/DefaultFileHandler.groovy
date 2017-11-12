package com.probtp.forge.dsl

import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils

import static org.apache.commons.io.FileUtils.*

class DefaultFileHandler implements FileHandler {

    private File file

    File getFile() {
        return file
    }

    void delete() {
        if (!file.exists()) {
            return
        }
        if (file.isDirectory()) {
            deleteDirectory(file)
        } else {
            deleteQuietly(file)
        }
    }

    /**
     * Override "--" operator to do the delete operation
     */
    DefaultFileHandler previous() {
        delete()
        return this
    }

    /**
     * Override ">>>" to do move operation
     * @param dir - If dir doesn't exist, it is created
     */
    void rightShiftUnsigned(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Destination file must be a Directory !")
        }
        if (!dir.exists()) {
            forceMkdir(dir)
        }
        if (file.isDirectory()) {
            moveDirectory(file, dir)
        } else {
            moveFileToDirectory(file, dir, true)
        }
    }

    void rightShiftUnsigned(FileHandler fileHandler) {
        rightShiftUnsigned(fileHandler.file)
    }

    void rightShiftUnsigned(String dir) {
        rightShiftUnsigned(new File(dir))
    }

    /**
     *
     * @param templateParameters
     */
    TemplateResultValue rightShift(Map<String, Object> templateParameters) {
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        String result = templateEngine.createTemplate(file).make(templateParameters)
        return new TemplateResultValue(value: result)
    }

    /**
     * Override ">>" to do copy operation
     * @param dir - If dir doesn't exist, it is created
     */
    void rightShift(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Destination file must be a Directory !")
        }
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir)
        }
        if (file.isDirectory()) {
            FileUtils.copyDirectory(file, dir)
        } else {
            FileUtils.copyFileToDirectory(file, dir)
        }
    }

    void rightShift(FileHandler fileHandler) {
        rightShift(fileHandler.getFile())
    }

    void rightShift(String strFile) {
        rightShift(new File(strFile))
    }

    void save() {
    }

    void saveTo(Writer writer) {
    }

    static DefaultFileHandler handle(File file) {
        DefaultFileHandler handler = new DefaultFileHandler()
        handler.file = file
        return handler
    }
}
