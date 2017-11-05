package com.probtp.forge.dsl

interface FileOperation {

    FileOperation append(Closure closure)

    FileOperation removeAll()

    FileOperation grep(Closure closure)

    FileOperation transform(Closure closure)
}
