package com._2horizon.cva.retrieval.twitter.timeline

import com._2horizon.cva.retrieval.event.TwitterBulkStatusEvent
import com._2horizon.cva.retrieval.twitter.api.TwitterApiService
import com._2horizon.cva.retrieval.twitter.config.TwitterConfig
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import twitter4j.Paging
import twitter4j.Status
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-04.
 */
@Singleton
@Requires(property = "app.twitter.crawler.enabled", value = "true")
class TwitterTimelineCrawler(
    private val twitterConfig: TwitterConfig,
    private val twitterApiService: TwitterApiService,
    private val eventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(TwitterTimelineCrawler::class.java)

    private val maxPages = 16 // The API restricts more than 16*200 = 3200

    private val liefraId = 85857730L
    private val ECMWF = 370094706L
    private val CopernicusECMWF = 3346529644L



    private val crawlerRunning = AtomicBoolean()

    @EventListener
    fun onStartupEvent(startupEvent: StartupEvent){
        userTimeline()
    }

    private fun getUserIds(): Mono<List<Long>> {
        return Mono.just(listOf(CopernicusECMWF))
    }



    // @Scheduled(cron = "\${app.twitter.crawler.cron}")
    fun userTimeline() {

        if (twitterConfig.crawler.enabled!!) {

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

                        eventPublisher.publishEvent(TwitterBulkStatusEvent(statuses))
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
