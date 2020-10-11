package com._2horizon.cva.dialogflow.fulfillment.extensions

import smile.nlp.pos.HMMPOSTagger
import smile.nlp.pos.PennTreebankPOS
import smile.nlp.tokenizer.SimpleTokenizer

/**
 * Created by Frank Lieber (liefra) on 2020-09-02.
 */
fun String.splitIntoWords(splitContraction: Boolean = false): List<String> {
    return SimpleTokenizer(splitContraction).split(this).toList()
}

fun posTagging(words: List<String>): List<PennTreebankPOS> {
    return HMMPOSTagger.getDefault().tag(words.toTypedArray()).toList()
}
