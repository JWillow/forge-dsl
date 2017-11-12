package com.probtp.forge.dsl

import org.apache.commons.io.FileUtils

class TemplateResultValue {

    String value

    private void write(File file) {
        if(file.isDirectory()) {
            throw new IllegalArgumentException("The destination must be a File, not a Directory")
        }
        FileUtils.write(file, value, "UTF-8")
    }

    void rightShift(String strFile) {
        write(new File(strFile))
    }

    void rightShift(File file) {
        write(file)
    }

    void rightShift(FileHandler handler) {
        write(handler.file)
    }

}
