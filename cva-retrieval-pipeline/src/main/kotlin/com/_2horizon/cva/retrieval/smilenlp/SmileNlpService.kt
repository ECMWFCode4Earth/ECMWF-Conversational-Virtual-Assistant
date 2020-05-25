package com._2horizon.cva.retrieval.smilenlp

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.londogard.smile.extensions.bag
import com.londogard.smile.extensions.keywords
import com.londogard.smile.extensions.normalize
import com.londogard.smile.extensions.sentences
import com.londogard.smile.extensions.words
import io.micronaut.context.annotation.Value
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.slf4j.LoggerFactory
import smile.nlp.Bigram
import smile.nlp.SimpleCorpus
import smile.nlp.Text
import smile.nlp.dictionary.EnglishPunctuations
import smile.nlp.dictionary.EnglishStopWords
import smile.nlp.tokenizer.PennTreebankTokenizer
import smile.nlp.tokenizer.SimpleSentenceSplitter
import java.io.File
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
@Singleton
class SmileNlpService(
    private val objectMapper: ObjectMapper,
    @Value("\${app.retrieval.ecmwf.publications-path}") private val publicationsPath: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun textToSentences(inputText: String): List<String> {

        val text = inputText.normalize()

        val sentences = text.sentences()

        val wordSegmentation = sentences.flatMap { sentence -> sentence.words() }

        val bagOfWords = text.bag()

        val keywords = text.keywords(50)

        val corpus = SimpleCorpus().apply { add(Text(inputText)) }
        val sortedTopTerms = getSortedTopTerms(corpus)
        val sortedBigrams = getSortedBigrams(corpus)

        return sentences
    }

    fun analyseEcmwfPublications(): Pair<List<Pair<String, Int>>, List<Pair<Bigram, Int>>> {
        val corpus = createLocalEcmwfPublicationFilePathCorpus()
        val sortedTopTerms = getSortedTopTerms(corpus)
        val sortedBigrams = getSortedBigrams(corpus)

       return Pair(sortedTopTerms,sortedBigrams)
    }

    private fun getSortedTopTerms(corpus: SimpleCorpus): List<Pair<String, Int>> =
        corpus.terms.asSequence().map { term -> Pair(term, corpus.getTermFrequency(term)) }.toList()
            .sortedByDescending { it.second }

    private fun getSortedBigrams(corpus: SimpleCorpus): List<Pair<Bigram, Int>> =
        corpus.bigrams.asSequence().map { bigram -> Pair(bigram, corpus.getBigramFrequency(bigram)) }.toList()
            .sortedByDescending { it.second }

    private fun createLocalEcmwfPublicationFilePathCorpus(): SimpleCorpus {
        val corpus = SimpleCorpus(
            SimpleSentenceSplitter.getInstance(),
            PennTreebankTokenizer.getInstance(),
            EnglishStopWords.DEFAULT,
            EnglishPunctuations.getInstance()
        )

        File(publicationsPath).listFiles()!!
            .forEach { file ->

                val pubDTO = objectMapper.readValue(file, EcmwfPublicationDTO::class.java)
                val title = pubDTO.title
                val secondaryTitle = pubDTO.secondaryTitle ?: ""
                val tertiaryTitle = pubDTO.tertiaryTitle ?: ""
                val cleanedAbstract = if (pubDTO.abstract != null) {
                    Jsoup.clean(pubDTO.abstract, Whitelist.none())
                } else {
                    ""
                }

                corpus.add(
                    Text(
                        """
                  $title
                  $secondaryTitle
                  $tertiaryTitle
                  $cleanedAbstract
                  
                """.trimIndent()
                    )
                )
            }

        return corpus
    }

    private fun createLocalFilePathCorpus(filePath: String): SimpleCorpus {
        val corpus = SimpleCorpus()

        File(filePath).listFiles()!!
            .forEach { file ->
                corpus.add(Text(file.readText()))
            }

        return corpus
    }

    fun getAllTextInBrackets(): List<Pair<String, Int>> {

        val reg = "\\((.*?)\\)".toRegex()

        return File(publicationsPath).listFiles()!!
            .map { file ->
                val pubDTO = objectMapper.readValue(file, EcmwfPublicationDTO::class.java)
                if (pubDTO.abstract != null) {
                    reg.findAll(pubDTO.abstract).map { it.value }.toList()
                } else {
                    emptyList()
                }
            }.flatten()
            .groupingBy { it }
            .eachCount()
            .map { abrev -> Pair(abrev.key, abrev.value) }.toList()
            .sortedByDescending { it.second }
    }
}
