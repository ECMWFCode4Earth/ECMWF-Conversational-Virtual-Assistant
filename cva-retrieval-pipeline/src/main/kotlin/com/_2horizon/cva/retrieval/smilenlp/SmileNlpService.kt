package com._2horizon.cva.retrieval.smilenlp

import com.londogard.smile.extensions.bag
import com.londogard.smile.extensions.keywords
import com.londogard.smile.extensions.normalize
import com.londogard.smile.extensions.sentences
import com.londogard.smile.extensions.words
import org.slf4j.LoggerFactory
import smile.nlp.Bigram
import smile.nlp.SimpleCorpus
import smile.nlp.Text
import java.io.File
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
@Singleton
class SmileNlpService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun textToSentences(inputText: String): List<String> {

        val text = inputText.normalize()

        val sentences = text.sentences()

        val wordSegmentation = sentences.flatMap { sentence -> sentence.words() }

        val bagOfWords = text.bag()

        val keywords = text.keywords()



        return sentences
    }

    private fun getSortedTopTerms(corpus: SimpleCorpus): List<Pair<String, Int>> =
        corpus.terms.asSequence().map { term -> Pair(term, corpus.getTermFrequency(term)) }.toList()
            .sortedByDescending { it.second }

    private fun getSortedBigrams(corpus: SimpleCorpus): List<Pair<Bigram, Int>> =
        corpus.bigrams.asSequence().map { bigram -> Pair(bigram, corpus.getBigramFrequency(bigram)) }.toList()
            .sortedByDescending { it.second }

    private fun createLocalFilePathCorpus(filePath: String): SimpleCorpus {
        val corpus = SimpleCorpus()

        File(filePath).listFiles()!!
            .forEach { file ->
                corpus.add(Text(file.readText()))
            }

        return corpus
    }
}
