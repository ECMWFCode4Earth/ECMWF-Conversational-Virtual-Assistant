package com._2horizon.cva.retrieval.confluence

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */

class StorageFormatUtil {
    companion object{

        @JvmOverloads
        @JvmStatic
        fun createDocumentFromStructuredStorageFormat(storageFormat: String, removeCodeBlock: Boolean = true): Document {
            val document = Jsoup.parse(storageFormat, "", Parser.xmlParser())

            if (removeCodeBlock) {
                document.select("ac|structured-macro[ac:name=code]").forEach { it.remove() }
            }
            return document
        }
    }
}
