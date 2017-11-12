package com.probtp.forge.dsl

interface FileHandler {

    File getFile()

    void save()

    void saveTo(Writer writer)
}