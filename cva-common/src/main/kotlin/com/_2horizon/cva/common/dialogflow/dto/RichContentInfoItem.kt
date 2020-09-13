package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentInfoItem(

    @JsonProperty("title")
    val title: String,

    @JsonProperty("subtitle")
    val subtitle: String?=null,

    @JsonProperty("actionLink")
    val actionLink: String,

    @JsonProperty("type")
    override val type: String = "info"
):RichContentItem
