package com._2horizon.cva.retrieval.confluence

import java.net.URI

/**
 * Created by Frank Lieber (liefra) on 2020-06-15.
 */
fun String?.isConfluencePageLink(): Boolean {
    val s = this.convertToCanonicalConfluenceLink()?.toLowerCase()
    if (s == null) {
        return false
    }
    return if (s.startsWith("https://confluence.ecmwf.int/display")) {
        val pathSegements = URI(this).path.split("/").filter { it.isNotBlank() }
        pathSegements.size > 2
    } else s.startsWith("https://confluence.ecmwf.int/pages/viewpage.action?pageid=")
}

fun String?.isConfluenceSpaceLink(): Boolean {
    val s = this.convertToCanonicalConfluenceLink()?.toLowerCase()
    if (s == null) {
        return false
    }
    return if (s.startsWith("https://confluence.ecmwf.int/display")) {
        val pathSegements = URI(this).path.split("/").filter { it.isNotBlank() }
        pathSegements.size == 2
    } else {
        false
    }
}

fun String?.isConfluenceLink(): Boolean {
    val s = this.convertToCanonicalConfluenceLink()?.toLowerCase()
    if (s == null) {
        return false
    }
    return s.startsWith("https://confluence.ecmwf.int/display/") || s.startsWith("https://confluence.ecmwf.int/pages/viewpage.action?pageId=")
}

fun String?.isNotConfluenceLink(): Boolean = !isConfluenceLink()

fun String?.convertToCanonicalConfluenceLink(): String? {
    if (this == null) {
        return null
    }
    return this
        .replace(
            "https://software.ecmwf.int/wiki/display/",
            "https://confluence.ecmwf.int//display/"
        )
        .replace(
            "https://confluence.ecmwf.int//display/",
            "https://confluence.ecmwf.int/display/"
        )
}
