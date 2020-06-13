package com._2horizon.cva.retrieval.nlp

import com._2horizon.cva.retrieval.confluence.StorageFormatUtil
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */
class SentencesDetectorTest extends Specification {


    @Shared
    SentencesDetector sentencesDetector = new SentencesDetector()

    @Unroll
    def "Should find sentences in storage text of #nodeId using BreakIteratorSentenceSplitter"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def sentences = sentencesDetector.findSentencesWithBreakIteratorSentenceSplitter(document.text())

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 82
        181122979 | 10
        171412993 | 3
    }


    @Unroll
    def "Should find sentences in storage text of #nodeId using SimpleSentenceSplitter"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def sentences = sentencesDetector.findSentencesWithSimpleSentenceSplitter(document.text())

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 82
        181122979 | 14
        171412993 | 4
    }

    @Unroll
    def "Should find sentences in storage text of #nodeId using CoreNLP"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def sentences = sentencesDetector.findCoreNlpSentences(document.text())

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 82
        181122979 | 13
        171412993 | 3
    }

    @Unroll
    def "Should find sentences in storage text of #nodeId using OoreNLP"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def sentences = sentencesDetector.findSentencesWithOpenNlp(document.text())

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 85
        181122979 | 13
        171412993 | 3
    }


}
