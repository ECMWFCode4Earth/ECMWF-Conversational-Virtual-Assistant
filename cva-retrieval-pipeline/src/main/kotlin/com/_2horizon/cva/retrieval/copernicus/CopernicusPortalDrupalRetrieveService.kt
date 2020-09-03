package com._2horizon.cva.retrieval.copernicus

import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.ContentSource
import com._2horizon.cva.common.elastic.baseUri
import com._2horizon.cva.retrieval.copernicus.c3s.portal.C3SPortalOperations
import com._2horizon.cva.retrieval.copernicus.cams.portal.CamsPortalOperations
import com._2horizon.cva.retrieval.event.BasicPageNodesEvent
import com._2horizon.cva.retrieval.event.ContentPageNodesEvent
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.event.annotation.EventListener
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
@Singleton

class CopernicusPortalDrupalRetrieveService(
    @Client("https://climate.copernicus.eu") private val c3sHttpClient: RxHttpClient,
    @Client("https://atmosphere.copernicus.eu") private val camsHttpClient: RxHttpClient,
    private val c3sPortalOperations: C3SPortalOperations,
    private val camsPortalOperations: CamsPortalOperations,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.copernicus.portal.c3s.enabled:false}") private val retrievalPipelinePortalC3SEnabled: Boolean,
    @Value("\${app.feature.retrieval-pipeline.copernicus.portal.cams.enabled:false}") private val retrievalPipelinePortalCAMSEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val crawlDelay: Duration = Duration.ofMillis(500)

    @EventListener
    fun onStartupEvent(startupEvent: StartupEvent) {
        if (retrievalPipelinePortalC3SEnabled) {

            retrieveRemotePageNodes(
                ContentSource.C3S,
                NodeType.NEWS,
                { page -> c3sPortalOperations.getNews(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.C3S,
                NodeType.PRESS_RELEASE,
                { page -> c3sPortalOperations.getPressReleases(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.C3S,
                NodeType.DEMONSTRATOR_PROJECT,
                { page -> c3sPortalOperations.getDemonstratorProjects(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.C3S,
                NodeType.CASE_STUDY,
                { page -> c3sPortalOperations.getCaseStudies(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.C3S,
                NodeType.EVENT,
                { page -> c3sPortalOperations.getEvents(page = page) },
                { html, contentSource, nodeType -> extractEvents(contentSource, nodeType, html) }
            )
        }
        if (retrievalPipelinePortalCAMSEnabled) {
            retrieveRemotePageNodes(
                ContentSource.CAMS,
                NodeType.NEWS,
                { page -> camsPortalOperations.getNews(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.CAMS,
                NodeType.PRESS_RELEASE,
                { page -> camsPortalOperations.getPressReleases(page = page) },
                { html, contentSource, nodeType -> extractPages(contentSource, nodeType, html) },
            )

            retrieveRemotePageNodes(
                ContentSource.CAMS,
                NodeType.EVENT,
                { page -> camsPortalOperations.getEvents(page = page) },
                { html, contentSource, nodeType -> extractEvents(contentSource, nodeType, html) },
            )
        }
    }

    private fun retrieveRemotePageNodes(
        contentSource: ContentSource,
        nodeType: NodeType,
        remoteOperation: (Int) -> Mono<String>,
        landingPageExtractOperation: (String, ContentSource, NodeType) -> Mono<List<CopernicusPageNode>>
    ) {

        val allPageNodes = mutableListOf<CopernicusPageNode>()

        Flux.range(0, 1000) // put end higher, as termination will be triggered by an error event
            .delayElements(crawlDelay)
            .flatMap { page ->
                log.debug("Going to get remote page #$page")
                remoteOperation(page)
            }
            .flatMap { html -> landingPageExtractOperation(html, contentSource, nodeType) }
            .doOnTerminate {
                log.info("Found ${allPageNodes.size} allPageNodes, going to publish BasicPageItemsEvent")
                applicationEventPublisher.publishEvent(BasicPageNodesEvent(contentSource, nodeType, allPageNodes))
            }
            .subscribe({ pageItems ->
                allPageNodes.addAll(pageItems)
            },
                { error -> log.warn(error.message) },
                {
                    log.info("Completed: found ${allPageNodes.size} allPageNodes")
                }
            )
    }

    @EventListener
    fun onBasicPageItemsEvent(basicPageNodesEvent: BasicPageNodesEvent) {

        Flux.fromIterable(basicPageNodesEvent.itemCopernicuses)
            .delayElements(crawlDelay)
            .flatMap { item ->

                val htmlFlowable = when (basicPageNodesEvent.contentSource) {
                    ContentSource.C3S -> {
                        log.debug("Going to fetch ${item.url}")
                        c3sHttpClient.retrieve(item.url)
                    }
                    ContentSource.CAMS -> {
                        log.debug("Going to fetch ${item.url}")
                        camsHttpClient.retrieve(item.url)
                    }
                    else -> {
                        error("ContentSource unknown basicPageItemsEvent.contentSource")
                    }
                }

                htmlFlowable.map { html ->
                    val contentElement = Jsoup.parse(html, basicPageNodesEvent.contentSource.baseUri()).selectFirst("section.main--section")
                    val contentHtml = contentElement.html()
                    val content = contentElement.text()
                    item.copy(contentHtml = contentHtml, contentStripped = content, content = "${item.title}. $content")
                }
            }
            .collectList()
            .subscribe { contentItems ->

                log.info("found ${contentItems.size} contentItems")
                applicationEventPublisher.publishEvent(
                    ContentPageNodesEvent(
                        basicPageNodesEvent.contentSource,
                        basicPageNodesEvent.nodeType,
                        contentItems
                    )
                )
            }
    }

    private fun extractEvents(
        contentSource: ContentSource,
        nodeType: NodeType,
        html: String
    ): Mono<List<CopernicusPageNode>> {

        val document = Jsoup.parse(html, contentSource.baseUri())

        val items = document.selectFirst("section.main--section").select("li.list--item")

        val pageItems = items.map { item ->

            val startDateElement = item.select("div.calendardate-start")
            val endDateElement = item.select("div.calendardate-end")

            val startDate = extractLocalDate(startDateElement)!!
            val endDate = extractLocalDate(endDateElement)

            val url = item.selectFirst("a[href]").attr("abs:href")
            val title = item.selectFirst("h3").text()

            val teaser = item.selectFirst("div.teaser").text()
            CopernicusPageNode(
                verifiedAt = LocalDateTime.now(ZoneId.of("UTC")),
                id = url,
                source = contentSource,
                content = title,
                dateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT),
                nodeType = nodeType,
                url = url,
                title = title,
                startDate = startDate,
                endDate = endDate,
                teaser = teaser
            )
        }

        return if (pageItems.isNotEmpty()) {
            Mono.just(pageItems)
        } else {
            Mono.error(IllegalStateException("No pageItems found anymore, stopping remote retrieval of landing pages"))
        }
    }

    private fun extractLocalDate(els: Elements): LocalDate? {
        if (els.isEmpty()) return null

        val el = els.first()
        val day = el.selectFirst(".calendardate-date").text()
        val month = el.selectFirst(".calendardate-month").text()
        val year = el.selectFirst(".calendardate-year").text()
        return LocalDate.parse(
            "$day $month $year", DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
        )
    }

    private fun extractPages(
        contentSource: ContentSource,
        nodeType: NodeType,
        html: String
    ): Mono<List<CopernicusPageNode>> {

        val document = Jsoup.parse(html, contentSource.baseUri())

        val items = document.selectFirst("section.main--section").select("li.list--item")

        val pageItems = items.map { item ->
            val url = item.selectFirst("a[href]").attr("abs:href")
            val img = item.selectFirst("img[src]").attr("abs:src")
            val title = item.selectFirst("h3").text()
            val publishedAt = LocalDate.parse(
                item.selectFirst("span.label").text(),
                DateTimeFormatter.ofPattern("d['st']['nd']['rd']['th'] MMMM yyyy", Locale.ENGLISH)
            )
            val teaser = item.selectFirst("div.teaser").text()
            CopernicusPageNode(
                verifiedAt = LocalDateTime.now(ZoneId.of("UTC")),
                id = url,
                source = contentSource,
                content = title,
                dateTime = LocalDateTime.of(publishedAt, LocalTime.MIDNIGHT),
                nodeType = nodeType,
                url = url,
                img = img,
                title = title,
                publishedAt = publishedAt,
                teaser = teaser
            )
        }

        return if (pageItems.isNotEmpty()) {
            Mono.just(pageItems)
        } else {
            Mono.error(IllegalStateException("No pageItems found anymore, stopping remote retrieval of landing pages"))
        }
    }
}


