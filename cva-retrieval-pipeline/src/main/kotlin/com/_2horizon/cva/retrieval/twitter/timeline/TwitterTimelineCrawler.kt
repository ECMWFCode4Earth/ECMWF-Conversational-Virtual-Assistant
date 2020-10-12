package com._2horizon.cva.retrieval.twitter.timeline

import com._2horizon.cva.common.elastic.ContentSource
import com._2horizon.cva.common.twitter.dto.Tweet
import com._2horizon.cva.retrieval.event.TwitterBulkStatusEvent
import com._2horizon.cva.retrieval.twitter.api.TwitterApiService
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import twitter4j.Paging
import twitter4j.Status
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-04.
 */
@Singleton
@Requires(property = "app.feature.retrieval-pipeline.twitter.crawler.enabled", value = "true")
class TwitterTimelineCrawler(
    private val twitterApiService: TwitterApiService,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.twitter.crawler.enabled}") private val crawlerEnabled: Boolean,
    @Value("\${app.feature.retrieval-pipeline.twitter.users}") private val users: List<Long>,
    @Value("\${app.feature.retrieval-pipeline.twitter.crawler.max-pages}") private val maxPages: Int,
) {

    private val log = LoggerFactory.getLogger(TwitterTimelineCrawler::class.java)

    private val crawlerRunning = AtomicBoolean()

    private fun getUserIds(): Mono<List<Long>> {
        return Mono.just(users)
    }

    @Scheduled(cron = "\${app.feature.retrieval-pipeline.twitter.crawler.cron}")
    fun userTimeline() {

        if (crawlerEnabled) {

            if (!crawlerRunning.get()) {
                crawlerRunning.set(true)

                val idsCount = AtomicInteger()

                val delayBetweenBufferBatches = if (maxPages == 16) 60 * 15L else 1L

                log.info("-------------------------------      Stating TwitterTimelineCrawler       -----------------------------------")

                getUserIds()
                    .doOnNext { idsCount.set(it.size) }
                    .flatMapIterable { it }
                    .onBackpressureBuffer()
                    .buffer(30)
                    // .delayElements(Duration.ofSeconds(delayBetweenBufferBatches))
                    .flatMapIterable { it }
                    .flatMap({ userId -> crawlUserTweets(userId) }, 4)
                    .map { statuses ->

                        log.debug("Done crawling ${statuses.size}. Remaining ${idsCount.decrementAndGet()}")

                        val tweets = statuses.map(::convertStatusToTweet)
                        eventPublisher.publishEvent(TwitterBulkStatusEvent(tweets))
                    }
                    .doOnError { "User timeline error ${it.message}" }
                    .doOnTerminate {
                        log.info("DONE with userTimeline for all users and $maxPages pages per user")
                        crawlerRunning.set(false)
                    }
                    .subscribe()
            } else {
                log.info("Skipping TwitterTimelineCrawler because another instance still running")
            }
        } else {
            log.warn("TwitterTimelineCrawler disabled")
        }
    }

    fun convertStatusToTweet(status: Status): Tweet {

        val id = status.id
        val text = status.text
        val source = status.source
        val retweetId = status.retweetedStatus?.id
        val createdAt = status.createdAt.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime()
        val userId = status.user.id
        val userScreenName = status.user.screenName
        val hashtags = status.hashtagEntities.map { it.text }
        val urls = status.urlEntities.map { it.url }
        val expandedUrls = status.urlEntities.map { it.expandedURL }
        val mediaURLs = status.mediaEntities.map { it.mediaURLHttps }
        val mediaExpandedUrls = status.mediaEntities.map { it.mediaURLHttps }

        return Tweet(
            verifiedAt = LocalDateTime.now(ZoneId.of("UTC")),
            id = id.toString(),
            source = ContentSource.TWITTER,
            content = text,
            dateTime = createdAt,
            tweetId = id,
            text = text,
            tweetSource = source,
            retweetId = retweetId,
            createdAt = createdAt,
            userId = userId,
            userScreenName = userScreenName,
            hashtags = hashtags,
            urls = urls,
            expandedUrls = expandedUrls,
            mediaURLs = mediaURLs,
            mediaExpandedUrls = mediaExpandedUrls,
        )
    }

    fun crawlUserTweets(userId: Long): Mono<MutableList<Status>> {
        var nextPage = true

        return Flux.fromIterable(1..30)
            .takeWhile { nextPage }
            .map { page ->

                val statuses = if (page <= maxPages) {
                    val statuses = twitterApiService.twitter.getUserTimeline(userId, Paging(page, 200))

                    if (statuses.size == 0) {
                        nextPage = false
                    }

                    statuses
                } else {
                    nextPage = false
                    emptyList<Status>()
                }

                statuses
            }
            .retryWhen(Retry.backoff(5, Duration.ofSeconds(1)))
            .flatMapIterable { it }
            .collectList()
            .subscribeOn(Schedulers.elastic())
            .doOnError { log.error(it.message) }
            .doOnNext { log.debug("Found ${it.size} tweets for user $userId") }
    }
}

