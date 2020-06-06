package com._2horizon.cva.retrieval.extensions

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
@JvmOverloads
fun String.extractTextInBrackets(size: Int = 70): List<TextInBrackets> {
    val reg = "([^)]*)\\((.{1,10})\\)".toRegex()  // to check use: https://regex101.com/

    return reg.findAll(this).map {
        check(it.groupValues.size == 3) { "Groups size not 3 but ${it.groupValues.size}" }

        val helper = "${it.groupValues[1].takeLast(size)} (**${it.groupValues[2]}**)"

        TextInBrackets(helper, it.groupValues[2])
    }.toList()
        
}

@JvmOverloads
fun String.extractUppercaseText(size: Int = 25): List<TextInBrackets> {
    val reg = """([^)]*)(\b[A-Z0-9][A-Z0-9]['a-zA-Z0-9]{0,8}|\b[A-Z]\b)([^)]*)""".toRegex() // to check use: https://regex101.com/

    return reg.findAll(this).map {
        check(it.groupValues.size == 4) { "Groups size not 4 but ${it.groupValues.size}" }

        val helper = "${it.groupValues[1].takeLast(size)}**${it.groupValues[2]}**${it.groupValues[3].take(size)}"

        TextInBrackets(helper, it.groupValues[2])
    }.toList()

}



data class TextInBrackets(
    val textPriorBrackets: String,
    val textInBrackets: String
)

