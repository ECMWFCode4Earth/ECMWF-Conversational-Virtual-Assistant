package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.dialogflow.fulfillment.copernicus.dto.CopernicusDataStoreStatus
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import java.util.Optional

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Client("https://cds.climate.copernicus.eu")
// @Retryable(attempts = "5", multiplier = "1.5")
interface CopernicusDataStoreStatusOperations {

    @Get("/live/activity/status")
    fun liveActivityStatus( ): Optional<CopernicusDataStoreStatus>
}
