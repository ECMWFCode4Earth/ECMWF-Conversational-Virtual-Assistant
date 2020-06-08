package com._2horizon.cva.retrieval.confluence.dto.pagechildren

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
data class PageChildrenResponse(
    @JsonProperty("page")
    val page: Page
)
