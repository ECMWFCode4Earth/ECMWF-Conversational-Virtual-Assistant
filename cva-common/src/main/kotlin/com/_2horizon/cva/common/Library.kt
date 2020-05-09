package com._2horizon.cva.common

import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader

fun String.sayHello() = this.toUpperCase()


fun String.loadClasspathResourceAsString(): String {
    return  String(
        ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
            .getResourceAsStream("classpath:$this")
            .get().readAllBytes())
}
