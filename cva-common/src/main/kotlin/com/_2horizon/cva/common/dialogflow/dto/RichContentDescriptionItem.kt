package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentDescriptionItem(

    @JsonProperty("title")
    val title: String,

    @JsonProperty("text")
    val text: List<String>,

    @JsonProperty("type")
    override val type: String = "description"
) : RichContentItem
