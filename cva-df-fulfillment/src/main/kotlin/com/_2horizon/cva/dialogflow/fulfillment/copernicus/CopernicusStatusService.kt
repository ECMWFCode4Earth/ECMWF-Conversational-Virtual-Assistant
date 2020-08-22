package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.dialogflow.fulfillment.copernicus.dto.CopernicusDataStoreStatus
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentDescriptionItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentItem
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.uri.UriBuilder
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Optional
import java.util.zip.GZIPInputStream
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Singleton
class CopernicusStatusService(
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(1))
        .build()

    fun retrieveStatus(): Optional<CopernicusDataStoreStatus> {

        return try {
            val copernicusDsUri = UriBuilder.of("https://cds.climate.copernicus.eu")
                .path("/live/activity/status")
                .build()

            val httpRequest = HttpRequest.newBuilder().GET().uri(copernicusDsUri).build()

            val response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofInputStream())

            val body = if (response.headers().firstValue("Content-Encoding").orElse("") == "gzip") {
                GZIPInputStream(response.body())
            } else {
                response.body()
            }

            Optional.of(objectMapper.readValue(body, CopernicusDataStoreStatus::class.java))
        } catch (ex: Throwable) {
            log.error("Couldn't retrieve CopernicusDataStoreStatus: ${ex.message}")
            Optional.empty()
        }
    }

    fun retrieveStatusAsRichContent(): RichContentItem {
        val statusOptional = retrieveStatus()
        return if (statusOptional.isPresent) {

            val status = statusOptional.get()

            val item = RichContentDescriptionItem(
                title = "CDS status at ${status.timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME)}",
                text = listOf(
                    "Running requests: ${status.running}",
                    "Queued requests: ${status.queued}",
                    "Total requests: ${status.running + status.queued}",
                    "Running users: ${status.runningUsers}",
                    "Queued users: ${status.queuedUsers}",
                    "Total users: ${status.totalUsers}"
                )
            )

            item
        } else {
            error("Cannot get copernicus status")
        }
    }
}
