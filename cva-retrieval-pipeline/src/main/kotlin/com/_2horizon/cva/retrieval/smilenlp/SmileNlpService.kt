package com._2horizon.cva.retrieval.smilenlp

import com.londogard.smile.extensions.bag
import com.londogard.smile.extensions.bag2
import com.londogard.smile.extensions.keywords
import com.londogard.smile.extensions.normalize
import com.londogard.smile.extensions.sentences
import com.londogard.smile.extensions.words
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
@Singleton
class SmileNlpService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun textToSentences(): List<String> {

        val unicode =
            """When airport foreman Scott Babcock went out onto the runway at Wiley Post-Will Rogers Memorial Airport in Utqiagvik, Alaska, on Monday to clear some snow, he was surprised to find a visitor waiting for him on the asphalt: a 450-pound bearded seal chilling in the milky sunshine.

“It was very strange to see the seal. I’ve seen a lot of things on runways, but never a seal,” Babcock told ABC News. His footage of the hefty mammal went viral after he posted it on Facebook.

According to local TV station KTVA, animal control was called in and eventually moved the seal with the help of a “sled.”

Normal air traffic soon resumed, the station said.

Poking fun at the seal’s surprise appearance, the Alaska Department of Transportation warned pilots on Tuesday of  “low sealings” in the North Slope region — a pun on “low ceilings,” a term used to describe low clouds and poor visibility.

Though this was the first seal sighting on the runway at the airport, the department said other animals, including birds, caribou and polar bears, have been spotted there in the past.

“Wildlife strikes to aircraft pose a significant safety hazard and cost the aviation industry hundreds of millions of dollars each year,” department spokeswoman Meadow Bailey told the Associated Press. “Birds make up over 90 percent of strikes in the U.S., while mammal strikes are rare.”"""

        val text = unicode.normalize()

        val sentences = text.sentences()

        val wordSegmentation = sentences.flatMap { sentence -> sentence.words() }

        val bagOfWords = text.bag()
        val bagOfWords2 = text.bag2()
        val keywords = text.keywords(2)

        return sentences
    }
}
