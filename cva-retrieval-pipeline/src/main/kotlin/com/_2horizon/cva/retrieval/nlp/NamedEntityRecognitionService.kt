package com._2horizon.cva.retrieval.nlp

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.slf4j.LoggerFactory
import java.util.Properties
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-13.
 */
@Singleton
class NamedEntityRecognitionService {
    private val log = LoggerFactory.getLogger(javaClass)



    fun detectNamedEntities(text: String) {

        val props = Properties().apply {
            setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,regexner")
            // setProperty("annotators", "tokenize,ssplit,regexner")
            setProperty("regexner.mapping", "nlp/corenlp/jg-regexner.tsv")
        }

        val pipeline = StanfordCoreNLP(props)

        val doc = CoreDocument(text)

        pipeline.annotate(doc)

        val entityMentions = doc.entityMentions()

        entityMentions.forEach { em ->

            println("\tdetected entity: \t" + em.text() + "\t" + em.entityType())

        }

        val tokens = doc.tokens()

        tokens.forEach { token ->
            println("${token.word()} ${token.ner()}")
        } .also {
            print("")
        }
    }
}
