package com._2horizon.cva.retrieval.extensions

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
@JvmOverloads
fun String.extractBrackets(size: Int = 25): List<TextInBrackets> {
    val reg = "(.{0,$size})\\((.*?)\\)".toRegex()
    val textInBrackets = reg.findAll(this).map {

        check(it.groupValues.size == 3) { "Groups size not 3 but ${it.groupValues.size}" }

        TextInBrackets(it.groupValues[1], it.groupValues[2])
    }.toList()

    return textInBrackets
}

data class TextInBrackets(
    val textPriorBrackets: String,
    val textInBrackets: String
)

