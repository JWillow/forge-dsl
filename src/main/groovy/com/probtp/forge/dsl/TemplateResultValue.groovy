package com.probtp.forge.dsl

import org.apache.commons.io.FileUtils

class TemplateResultValue {

    String value

    private void write(File file, boolean append) {
        if(file.isDirectory()) {
            throw new IllegalArgumentException("The destination must be a File, not a Directory")
        }
        FileUtils.writeStringToFile(file, value, "UTF-8", append)
    }

    void rightShift(String strFile) {
        write(new File(strFile), true)
    }

    void rightShift(File file) {
        write(file, true)
    }

    void rightShift(FileHandler handler) {
        write(handler.file, true)
    }

    void rightShiftUnsigned(String strFile) {
        write(new File(strFile), false)
    }

    void rightShiftUnsigned(File file) {
        write(file, false)
    }

    void rightShiftUnsigned(FileHandler handler) {
        write(handler.file, false)
    }

}
