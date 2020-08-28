package com._2horizon.cva.nlp

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
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

    private val openNlpSentenceModel = ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
        .getResource("classpath:nlp/opennlp/en-sent.bin").get()

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

    fun findSentencesWithOpenNlp(text: String): List<String> {
        val sentenceDetector = SentenceDetectorME(SentenceModel(openNlpSentenceModel))

        val sentences = sentenceDetector.sentDetect(text)
        log.info("Detected ${sentences.size} sentences")
        return sentences.toList()
    }
}
