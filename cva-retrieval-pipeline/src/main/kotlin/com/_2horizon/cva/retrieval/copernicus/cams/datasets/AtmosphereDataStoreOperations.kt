package com._2horizon.cva.retrieval.copernicus.cams.datasets

import com._2horizon.cva.retrieval.copernicus.dto.Resource
import com._2horizon.cva.retrieval.copernicus.dto.TermsList
import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable
import java.util.Optional

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@Client("https://ads.atmosphere.copernicus.eu/api")
@Retryable(attempts = "5", multiplier = "1.5")
interface AtmosphereDataStoreOperations {

    @Get("/v2/resources")
    fun getResources(): Optional<List<String>>

    @Get("/v2.ui/resources")
    fun getUiResources(): Optional<List<String>>

    @Get("/v2/resources/{key}")
    fun getResourceByKey(key: String): Optional<Resource>

    @Get("/v2.ui/resources/{key}")
    fun getUiResourceByKey(key: String): Optional<UiResource>

    @Get("/v2/terms/list")
    fun getTermsList(): Optional<TermsList>

    @Get("/v2.ui/terms/list")
    fun getUiTermsList(): Optional<TermsList>


}



