package com.probtp.forge.dsl

import groovy.transform.Immutable

@Immutable
class Project {

    File rootDir

    File getAt(String pathExpression) {
        rootDir.eachFileMatch()
    }


}
