package com._2horizon.cva.retrieval.nlp

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.slf4j.LoggerFactory
import smile.nlp.tokenizer.BreakIteratorSentenceSplitter
import smile.nlp.tokenizer.SimpleSentenceSplitter
import java.util.Properties
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */
@Singleton
class SentencesDetector {
    private val log = LoggerFactory.getLogger(javaClass)

    fun findSentencesWithSimpleSentenceSplitter(text: String): List<String> =
        SimpleSentenceSplitter.getInstance().split(text).toList()

    fun findSentencesWithBreakIteratorSentenceSplitter(text: String): List<String> =
        BreakIteratorSentenceSplitter().split(text).toList()

    fun findCoreNlpSentences(text: String): List<String> {
        val coreDocument = CoreDocument(text)

        StanfordCoreNLP(Properties().apply {
            setProperty("annotators", "tokenize,ssplit")
        }).annotate(coreDocument)

        val coreSentences = coreDocument.sentences()

        return coreSentences.map { it.text() }
    }
}
