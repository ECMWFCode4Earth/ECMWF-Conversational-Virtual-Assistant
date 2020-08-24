package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.copernicus.CopernicusDataStoreSolrSearchService
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentSuggestionChipsItem
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Singleton
class CopernicusFulfillmentService(
    objectMapper: ObjectMapper,
    private val copernicusStatusService: CopernicusStatusService ,
    private val copernicusDataStoreSolrSearchService: CopernicusDataStoreSolrSearchService
) : AbstractFulfillmentService(objectMapper) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handle(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val session = fulfillmentChain.webhookRequest.session
        val action = fulfillmentChain.webhookRequest.queryResult.action

        return when (fulfillmentChain.fulfillmentState) {
            // "cds.status" -> {
            //     retrieveStatusAsRichContent(webhookRequest)
            // }
            // "application.list" -> {
            //     retrieveAppsAsRichContent(webhookRequest)
            // }
            FulfillmentState.CF_CDS_DATASET_EXECUTE_DATASET_SEARCH,FulfillmentState.CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK -> {
                // retrieveAppsAsRichContent(webhookRequest)
                retrieveDatasetsAsRichContent(fulfillmentChain)
            }

            else -> {
               error("CopernicusFulfillmentService couldn't handle state ${fulfillmentChain.fulfillmentState}")
            }
        }
    }

    private fun retrieveDatasetsAsRichContent(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val queryText = fulfillmentChain.webhookRequest.queryResult.queryText

        val datasets = copernicusDataStoreSolrSearchService.searchDatasetsByQueryTerm(queryText)

        val image = RichContentSuggestionChipsItem.ChipOption.ChipImage(
            src = RichContentSuggestionChipsItem.ChipOption.ChipImage.ChipImageSrc(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALUAAACtCAMAAADBCM5tAAAAUVBMVEUAAADu7u7////l5eWZmZk5OTk/Pz9ycnJ/f3+srKy/v7+6urodHR2goKBQUFAQEBDIyMhgYGBWVlaQkJDX19dlZWXPz88rKyvf398wMDBHR0dcj3rfAAADO0lEQVR4nO3c7XKqMBAGYOg5EiIEEPzs/V9oBUpEiDYkkOzGfX9lGtSnDOyiE4hijIl8A4xCanchtbuQ2l1wq/+DzVv1F9D8of7XptsQ0ojUpP5ENRDmdERqUntXVykTbtX2HyCK6J6yzlbc4Rurs7qMhtyaGIP6mObRcwoOXc2LSJHyVMFVn1mpMne5tEfKtmqTt43F7SW5Ty2gVT5++IPcJWdXOOrqND0BXycRMQR13Oy0yV3KA/et3h9en4Cvk6eVv954ZfpHxjRFs3iHr6IWytKsn3u7d63OjI6Mafoi7kg9b9rmKbgbNf9ejdyla/fb9sbstMaRMc1NxDo73Ewdi8sG5D5tu99Crde0zZOzam31kqZtnrbdr6Y+Lm3a5ikPe2P10+a83uIEfJ28udpWvsqiaZunEDZq26ZtnrbdG6nPqzRt81zSeKl6zaZtnnu7X6CONy7N+ilT/Rri7XBWRGirfUvHOaBUJ6R2lp12DfEtHUdfDaFWDzlpq0++qaNk+r0xY4yl9yTesHUvaK76vVH2I+ZNvbf4VkDqT1CfLdTcFzq3+t7oaWeX2QS87HujrIEpY+5GjfKnqDDXh/gXfo4aCHM6WqzOdkmbXZvfUSNnH38bRo8LtfEr+hGT78yS2ey6akXhZnJ2Pve4KJ7PJfKdFddmpMahnr9wP/+AVM6qZMNrVf/R8M6K35ffnZKkJvUnqnHWEFKTOkQ1zhpCalKHqMZZQ0hN6hDVOGsIqUkdohpnDSE1qUNU46whpCZ1iGqcNYTUpA5RjbOGkJrUIapx1hBSkzpENc4agl6dpd16V8XtbIlcDTufy+VaWcWcXEmruFWkX13LLVfXKkQuklutZFYcGG5yMVDLzf2t0L9aVD66G4LUAanl5p4K3z3covLhVIMakRqOGghzOtJR89THzVSjEa8Wq1O/D7Tos+OL1LG/G3af0yxRw3k8BNdXe7sZc55cv4as/MwvqwhtNYQzcQjTVvuWjlOjVON8pgXO54eErgZVQ7TVYB73FPWPLtBTH5vv2a24fkbsrN8boVxQP49ITWo7NRDmdERqUn+qGl7eq8HmnRpXSO0upHYXUrsLqd0Fp/oHw+u1ogCCMAgAAAAASUVORK5CYII="
            )
        )

      val chips =   datasets.map { dataset->
            RichContentSuggestionChipsItem.ChipOption(
                text = dataset.title,
                link = "https://cds.climate.copernicus.eu/cdsapp#!/dataset/${dataset.id}?tab=overview",
                image = image
            )

        }

        return if (chips.isNotEmpty()) {
             convertRichContentItemToWebhookResponse(RichContentSuggestionChipsItem(chips.take(5)), fulfillmentChain.webhookRequest,fulfillmentChain.webhookResponseBuilder)
        } else {
            error("retrieveDatasetsAsRichContent")
        }

    }

    private fun retrieveAppsAsRichContent(webhookRequest: WebhookRequest,webhookResponseBuilder: WebhookResponse.Builder,): WebhookResponse.Builder {

        val image = RichContentSuggestionChipsItem.ChipOption.ChipImage(
            src = RichContentSuggestionChipsItem.ChipOption.ChipImage.ChipImageSrc(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALUAAACtCAMAAADBCM5tAAAAUVBMVEUAAADu7u7////l5eWZmZk5OTk/Pz9ycnJ/f3+srKy/v7+6urodHR2goKBQUFAQEBDIyMhgYGBWVlaQkJDX19dlZWXPz88rKyvf398wMDBHR0dcj3rfAAADO0lEQVR4nO3c7XKqMBAGYOg5EiIEEPzs/V9oBUpEiDYkkOzGfX9lGtSnDOyiE4hijIl8A4xCanchtbuQ2l1wq/+DzVv1F9D8of7XptsQ0ojUpP5ENRDmdERqUntXVykTbtX2HyCK6J6yzlbc4Rurs7qMhtyaGIP6mObRcwoOXc2LSJHyVMFVn1mpMne5tEfKtmqTt43F7SW5Ty2gVT5++IPcJWdXOOrqND0BXycRMQR13Oy0yV3KA/et3h9en4Cvk6eVv954ZfpHxjRFs3iHr6IWytKsn3u7d63OjI6Mafoi7kg9b9rmKbgbNf9ejdyla/fb9sbstMaRMc1NxDo73Ewdi8sG5D5tu99Crde0zZOzam31kqZtnrbdr6Y+Lm3a5ikPe2P10+a83uIEfJ28udpWvsqiaZunEDZq26ZtnrbdG6nPqzRt81zSeKl6zaZtnnu7X6CONy7N+ilT/Rri7XBWRGirfUvHOaBUJ6R2lp12DfEtHUdfDaFWDzlpq0++qaNk+r0xY4yl9yTesHUvaK76vVH2I+ZNvbf4VkDqT1CfLdTcFzq3+t7oaWeX2QS87HujrIEpY+5GjfKnqDDXh/gXfo4aCHM6WqzOdkmbXZvfUSNnH38bRo8LtfEr+hGT78yS2ey6akXhZnJ2Pve4KJ7PJfKdFddmpMahnr9wP/+AVM6qZMNrVf/R8M6K35ffnZKkJvUnqnHWEFKTOkQ1zhpCalKHqMZZQ0hN6hDVOGsIqUkdohpnDSE1qUNU46whpCZ1iGqcNYTUpA5RjbOGkJrUIapx1hBSkzpENc4agl6dpd16V8XtbIlcDTufy+VaWcWcXEmruFWkX13LLVfXKkQuklutZFYcGG5yMVDLzf2t0L9aVD66G4LUAanl5p4K3z3covLhVIMakRqOGghzOtJR89THzVSjEa8Wq1O/D7Tos+OL1LG/G3af0yxRw3k8BNdXe7sZc55cv4as/MwvqwhtNYQzcQjTVvuWjlOjVON8pgXO54eErgZVQ7TVYB73FPWPLtBTH5vv2a24fkbsrN8boVxQP49ITWo7NRDmdERqUn+qGl7eq8HmnRpXSO0upHYXUrsLqd0Fp/oHw+u1ogCCMAgAAAAASUVORK5CYII="
            )
        )

        val item = RichContentSuggestionChipsItem(
            options = listOf(
                RichContentSuggestionChipsItem.ChipOption(
                    text = "ERA5 explorer",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-era5-explorer",
                    image = image
                ),
                RichContentSuggestionChipsItem.ChipOption(
                    text = "Climate projections of navigability for the Arctic Northeast Passage",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-arctic-route-availability-projections",
                    image = image
                ),
                RichContentSuggestionChipsItem.ChipOption(
                    text = "Navigability of the Arctic Northeast Passage from 1993 to 2018",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-arctic-route-availability-historical",
                    image = image
                ),
                RichContentSuggestionChipsItem.ChipOption(
                    text = "Climate projections of Arctic sea ice extent",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-arctic-sea-ice-extent-projections",
                    image = image
                ),
                RichContentSuggestionChipsItem.ChipOption(
                    text = "Projections of sailing limits for different ice-class vessels",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-ice-class-limits",
                    image = image
                ),
                RichContentSuggestionChipsItem.ChipOption(
                    text = "Arctic sea ice extent from 1993 to 2018",
                    link = "https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-arctic-sea-ice-extent-historical",
                    image = image
                )

            )
        )
        return convertRichContentItemToWebhookResponse(item, webhookRequest,webhookResponseBuilder)
    }

    private fun retrieveStatusAsRichContent(webhookRequest: WebhookRequest,webhookResponseBuilder: WebhookResponse.Builder,): WebhookResponse.Builder {
        val item = copernicusStatusService.retrieveStatusAsRichContent()
        return convertRichContentItemToWebhookResponse(item, webhookRequest,webhookResponseBuilder)
    }
}
