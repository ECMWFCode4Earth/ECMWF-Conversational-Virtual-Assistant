package com._2horizon.cva.common.confluence.dto.pagechildren

import com.fasterxml.jackson.annotation.JsonProperty

data class Children(

    @JsonProperty("page")
    val page: Page
)
