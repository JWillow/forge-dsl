package com.probtp.forge.dsl

interface FileHandler {
    void save()

    void saveTo(Writer writer)
}