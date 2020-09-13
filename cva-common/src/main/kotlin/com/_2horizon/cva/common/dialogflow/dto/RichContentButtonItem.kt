package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentButtonItem(
    @JsonProperty("text")
    val text: String,

    @JsonProperty("link")
    val link: String? = null,

    @JsonProperty("icon")
    val icon: RichContentIcon? = null,

    @JsonProperty("event")
    val event: Event? = null,

    @JsonProperty("type")
    override val type: String = "button"
) : RichContentItem

