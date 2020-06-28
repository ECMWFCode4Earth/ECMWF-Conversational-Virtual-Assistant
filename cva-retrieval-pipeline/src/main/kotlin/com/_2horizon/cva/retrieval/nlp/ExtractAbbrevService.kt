package com._2horizon.cva.retrieval.nlp

import java.io.BufferedReader
import java.io.StringReader
import java.util.HashMap
import java.util.StringTokenizer
import javax.inject.Singleton

/**
 * The ExtractAbbrev class implements a simple algorithm for
 * extraction of abbreviations and their definitions from biomedical text.
 * Abbreviations (short forms) are extracted from the input file, and those abbreviations
 * for which a definition (long form) is found are printed out, along with that definition,
 * one per line.
 *
 *
 * A file consisting of short-form/long-form pairs (tab separated) can be specified
 * in tandem with the -testlist option for the purposes of evaluating the algorithm.
 *
 * @author Ariel Schwartz
 * @version 03/12/03
 * @see [A Simple Algorithm for Identifying Abbreviation Definitions in Biomedical Text](http://biotext.berkeley.edu/papers/psb03.pdf)
 * A.S. Schwartz, M.A. Hearst; Pacific Symposium on Biocomputing 8:451-462
 *
 * Convert to Kotlin by Frank Lieber
 *
 * @see [https://biotext.berkeley.edu/code/abbrev/ExtractAbbrev.java](https://biotext.berkeley.edu/code/abbrev/ExtractAbbrev.java)
 */
@Singleton
class ExtractAbbrevService {
    private fun isValidShortForm(str: String): Boolean {
        return hasLetter(str) && (Character.isLetterOrDigit(str[0]) || str[0] == '(')
    }

    private fun hasLetter(str: String): Boolean {
        for (element in str) if (Character.isLetter(element)) return true
        return false
    }

    private fun hasCapital(str: String): Boolean {
        for (element in str) if (Character.isUpperCase(element)) return true
        return false
    }

    fun extractAbbrPairs(inFile: String): HashMap<String, String?> {
        val abrevs = HashMap<String, String?>()
        var str: String
        var tmpStr: String
        var longForm = ""
        var shortForm = ""
        var currSentence = ""
        var openParenIndex: Int
        var closeParenIndex = -1
        var sentenceEnd= -1
        var newCloseParenIndex= -1
        var tmpIndex = -1
        var newParagraph = true
        var shortTokenizer: StringTokenizer
        try {
            val fin = BufferedReader(StringReader(inFile))
            while (fin.readLine().also { str = it } != null) {
                if (str.isEmpty() || newParagraph &&
                    !Character.isUpperCase(str[0])
                ) {
                    currSentence = ""
                    newParagraph = true
                    continue
                }
                newParagraph = false
                str += " "
                currSentence += str
                openParenIndex = currSentence.indexOf(" (")
                do {
                    if (openParenIndex > -1) openParenIndex++
                    sentenceEnd = currSentence.lastIndexOf(". ").coerceAtLeast(currSentence.lastIndexOf(", "))
                    if (openParenIndex == -1 && sentenceEnd == -1) {
                        //Do nothing
                    } else if (openParenIndex == -1) {
                        currSentence = currSentence.substring(sentenceEnd + 2)
                    } else if (currSentence.indexOf(')', openParenIndex).also { closeParenIndex = it } > -1) {
                        sentenceEnd = currSentence.lastIndexOf(". ", openParenIndex)
                            .coerceAtLeast(currSentence.lastIndexOf(", ", openParenIndex))
                        if (sentenceEnd == -1) sentenceEnd = -2
                        longForm = currSentence.substring(sentenceEnd + 2, openParenIndex)
                        shortForm = currSentence.substring(openParenIndex + 1, closeParenIndex)
                    }
                    if (shortForm.isNotEmpty() || longForm.isNotEmpty()) {
                        if (shortForm.length > 1 && longForm.length > 1) {
                            if (shortForm.indexOf('(') > -1 &&
                                currSentence.indexOf(')', closeParenIndex + 1)
                                    .also { newCloseParenIndex = it } > -1
                            ) {
                                shortForm = currSentence.substring(openParenIndex + 1, newCloseParenIndex)
                                closeParenIndex = newCloseParenIndex
                            }
                            if (shortForm.indexOf(", ").also { tmpIndex = it } > -1) shortForm =
                                shortForm.substring(0, tmpIndex)
                            if (shortForm.indexOf("; ").also { tmpIndex = it } > -1) shortForm =
                                shortForm.substring(0, tmpIndex)
                            shortTokenizer = StringTokenizer(shortForm)
                            if (shortTokenizer.countTokens() > 2 || shortForm.length > longForm.length) {
                                // Long form in ( )
                                tmpIndex = currSentence.lastIndexOf(" ", openParenIndex - 2)
                                tmpStr = currSentence.substring(tmpIndex + 1, openParenIndex - 1)
                                longForm = shortForm
                                shortForm = tmpStr
                                if (!hasCapital(shortForm)) shortForm = ""
                            }
                            if (isValidShortForm(shortForm)) {
                                if (extractAbbrPair(shortForm.trim { it <= ' ' }, longForm.trim { it <= ' ' })) {
                                    abrevs[shortForm.trim { it <= ' ' }] =
                                        findBestLongForm(shortForm.trim { it <= ' ' }, longForm.trim { it <= ' ' })
                                }
                            }
                        }
                        currSentence = currSentence.substring(closeParenIndex + 1)
                    } else if (openParenIndex > -1) {
                        if (currSentence.length - openParenIndex > 200) // Matching close paren was not found
                            currSentence = currSentence.substring(openParenIndex + 1)
                        break // Read next line
                    }
                    shortForm = ""
                    longForm = ""
                } while (currSentence.indexOf(" (").also { openParenIndex = it } > -1)
            }
            fin.close()
        } catch (ioe: Exception) {
            ioe.printStackTrace()
            println(currSentence)
            println(tmpIndex)
        }
        return abrevs
    }

    private fun findBestLongForm(shortForm: String, longForm: String): String? {
        var currChar: Char
        var sIndex: Int = shortForm.length - 1
        var lIndex: Int = longForm.length - 1
        while (sIndex >= 0) {
            currChar = Character.toLowerCase(shortForm[sIndex])
            if (!Character.isLetterOrDigit(currChar)) {
                sIndex--
                continue
            }
            while (lIndex >= 0 && Character.toLowerCase(longForm[lIndex]) != currChar ||
                sIndex == 0 && lIndex > 0 && Character.isLetterOrDigit(longForm[lIndex - 1])
            ) lIndex--
            if (lIndex < 0) return null
            lIndex--
            sIndex--
        }
        lIndex = longForm.lastIndexOf(" ", lIndex) + 1
        return longForm.substring(lIndex)
    }

    private fun extractAbbrPair(shortForm: String, longForm: String): Boolean {
        val tokenizer: StringTokenizer
        val longFormSize: Int
        if (shortForm.length == 1) return false
        val bestLongForm: String = findBestLongForm(shortForm, longForm) ?: return false
        tokenizer = StringTokenizer(bestLongForm, " \t\n\r\t-")
        longFormSize = tokenizer.countTokens()
        var shortFormSize: Int = shortForm.length
        for (i in shortFormSize - 1 downTo 0) if (!Character.isLetterOrDigit(shortForm[i])) shortFormSize--
        if (bestLongForm.length < shortForm.length || bestLongForm.indexOf("$shortForm ") > -1 ||
            bestLongForm.endsWith(shortForm) || longFormSize > shortFormSize * 2 || longFormSize > shortFormSize + 5 || shortFormSize > 10
        ) return false else {
            println("$shortForm/t$bestLongForm")
        }
        return true
    }
}
