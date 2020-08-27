package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.common.confluence.dto.version.ContentVersion
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable
import java.util.Optional

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Client("https://confluence.ecmwf.int/rest/experimental")
@Retryable(attempts = "5", multiplier = "1.5")
interface ConfluenceExperimentalOperations {

    @Get("/content/{contentId}/version/{version}")
    fun contentVersion(
        contentId:Long,
        version: Int
    ): Optional<ContentVersion>

}
