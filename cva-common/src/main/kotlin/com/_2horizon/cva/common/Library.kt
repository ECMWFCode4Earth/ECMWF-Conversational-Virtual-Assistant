package com._2horizon.cva.common

import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader



fun String.loadClasspathResourceAsString(): String {
    return  String(
        ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
            .getResourceAsStream("classpath:$this")
            .get().readAllBytes())
}
