package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.common.dialogflow.dto.RichContentButtonItem
import com._2horizon.cva.common.dialogflow.dto.RichContentDividerItem
import com._2horizon.cva.common.dialogflow.dto.RichContentIcon
import com._2horizon.cva.common.dialogflow.dto.RichContentInfoItem
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.copernicus.CopernicusDataStoreAsyncSolrSearchService
import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Singleton
class CopernicusFulfillmentService(
    objectMapper: ObjectMapper,
    private val copernicusDataStoreAsyncSolrSearchService: CopernicusDataStoreAsyncSolrSearchService,
) : AbstractFulfillmentService(objectMapper) {

    private val log = LoggerFactory.getLogger(javaClass)

    internal fun applicationSelected(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {
        val parameters = fulfillmentChain.dialogflowConversionStep.parameters
        val cdsApplication = parameters["cds_application"]

        return if (cdsApplication != null && cdsApplication.isNotBlank()) {
            val searchResult: Mono<CopernicusSolrResult> =
                copernicusDataStoreAsyncSolrSearchService.findApplicationById(cdsApplication)
            assembleDatasetOrApplicationResponse(searchResult, "application", "software", fulfillmentChain)
        } else {
            cdsEntryPointFallback(fulfillmentChain, "application")
        }
    }

    internal fun datasetSelected(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {
        val parameters = fulfillmentChain.dialogflowConversionStep.parameters
        val cdsDataset = parameters["cds_dataset"]

        return if (cdsDataset != null && cdsDataset.isNotBlank()) {
            val searchResult: Mono<CopernicusSolrResult> =
                copernicusDataStoreAsyncSolrSearchService.findDatasetById(cdsDataset)
            assembleDatasetOrApplicationResponse(searchResult, "dataset", "dataset", fulfillmentChain)
        } else {
            cdsEntryPointFallback(fulfillmentChain, "dataset")
        }
    }

    private fun cdsEntryPointFallback(fulfillmentChain: FulfillmentChain, type: String): Mono<WebhookResponse.Builder> {
        val button = RichContentButtonItem(
            text = "Click to visit the Climate Data Store",
            link = "https://cds.climate.copernicus.eu/cdsapp#!/search?type=$type",
            RichContentIcon(type = "chevron_right", color = "#FF9800")
        )
        val r = convertRichContentItemToWebhookResponse(
            listOf(button),
            fulfillmentChain.dialogflowConversionStep,
            fulfillmentChain.webhookResponseBuilder,
            listOf("I think you are looking for a $type in the Climate Data Store, but I'm not 100% sure which one. Please visit the CDS catalogue to see all available ${type}s".asIntentMessage())
        )
        return Mono.just(r)
    }

    private fun assembleDatasetOrApplicationResponse(
        searchResult: Mono<CopernicusSolrResult>,
        type: String,
        linkPart: String,
        fulfillmentChain: FulfillmentChain
    ): Mono<WebhookResponse.Builder> {
        return searchResult.map { dataset ->

            val datasetUrlSegment = dataset.id.split(".").last()

            val richContentItems = mutableListOf<RichContentItem>()
            richContentItems.add(
                RichContentInfoItem(
                    title = "View $type overview and license",
                    actionLink = "https://cds.climate.copernicus.eu/cdsapp#!/$linkPart/$datasetUrlSegment?tab=overview"
                )
            )
            richContentItems.add(RichContentDividerItem())
            richContentItems.add(
                RichContentInfoItem(
                    title = "View $type documentation",
                    actionLink = "https://cds.climate.copernicus.eu/cdsapp#!/$linkPart/$datasetUrlSegment?tab=doc"
                )
            )
            if (type == "dataset") {
                richContentItems.add(RichContentDividerItem())
                richContentItems.add(
                    RichContentInfoItem(
                        title = "Download $type using your web browser, CDS API or Toolbox",
                        actionLink = "https://cds.climate.copernicus.eu/cdsapp#!/$linkPart/$datasetUrlSegment?tab=form"
                    )
                )
            }

            if (type == "application") {
                richContentItems.add(RichContentDividerItem())
                richContentItems.add(
                    RichContentInfoItem(
                        title = "View $type",
                        actionLink = "https://cds.climate.copernicus.eu/cdsapp#!/$linkPart/$datasetUrlSegment?tab=app"
                    )
                )
                richContentItems.add(RichContentDividerItem())
                richContentItems.add(
                    RichContentInfoItem(
                        title = "View $type source code",
                        actionLink = "https://cds.climate.copernicus.eu/cdsapp#!/$linkPart/$datasetUrlSegment?tab=appcode"
                    )
                )
            }

            convertRichContentItemToWebhookResponse(

                richContentItems,
                fulfillmentChain.dialogflowConversionStep,
                fulfillmentChain.webhookResponseBuilder,
                listOf("I found the ${dataset.title} $type in the Climate Data Store. You can choose one of the following options".asIntentMessage())
            )

        }
    }
}
