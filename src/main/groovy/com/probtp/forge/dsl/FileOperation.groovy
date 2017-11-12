package com.probtp.forge.dsl

interface FileOperation {

    FileOperation append(Closure closure)

    FileOperation append(File file)

    FileOperation append(File file, Map<String,Object> parameters)

    FileOperation removeAll()

    FileOperation grep(Closure closure)

    FileOperation transform(Closure closure)

    int size()
}
