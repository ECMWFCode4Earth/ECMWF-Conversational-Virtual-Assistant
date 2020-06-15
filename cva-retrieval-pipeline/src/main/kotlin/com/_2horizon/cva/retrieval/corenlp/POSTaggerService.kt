package com._2horizon.cva.retrieval.corenlp

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.simple.Sentence
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory
import io.micronaut.context.event.StartupEvent
import org.slf4j.LoggerFactory
import java.util.Properties
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-18.
 */
@Singleton
class POSTaggerService {
    private val log = LoggerFactory.getLogger(javaClass)

    // set up pipeline properties
    val props = Properties().apply {
        setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse")
        // setProperty("parse.maxlen", "100")
        // setProperty("coref.algorithm", "neural")
    }

    // @EventListener
    fun onStartup(startupEvent: StartupEvent) {
    }

    @JvmOverloads
    fun questionDetector(text: String, skippingLength: Int = 250): Boolean {
        log.debug("questionDetector for $text")

        if (text.length > skippingLength) {
            log.warn("skipping questionDetector for $text")
            return false
        }

        val sentenceSimple = Sentence(text, Properties().apply { setProperty("pos.maxlen", "100") })

        //SBARQ: Direct question introduced by wh-element
        //SQ: Yes/no questions and subconstituent of SBARQ excluding wh-element

        val t = sentenceSimple.parse().constituents(LabeledScoredConstituentFactory())
        return t.map { it.label().value() }.any { it == "SBARQ" || it == "SQ" }
    }

    fun questionDetectorStanfordCoreNLP(text: String): Boolean {

        // build pipeline
        val pipeline = StanfordCoreNLP(props)

        // create a document object
        val document = CoreDocument(text)

        // annnotate the document
        pipeline.annotate(document)

        // second sentence
        val sentence = document.sentences()[0]

        val posTags: List<String> = sentence.posTags()
        val constituencyParse = sentence.constituencyParse()

        val t = constituencyParse.constituents(LabeledScoredConstituentFactory())
        return t.map { it.label().value() }.any { it == "SBARQ" || it == "SQ" }
    }
}
