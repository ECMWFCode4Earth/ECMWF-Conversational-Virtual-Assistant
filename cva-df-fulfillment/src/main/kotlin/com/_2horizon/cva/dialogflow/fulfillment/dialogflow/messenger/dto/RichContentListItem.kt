package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentListItem(
    @JsonProperty("title")
    val title: String,

    @JsonProperty("subtitle")
    val subtitle: String? = null,

    @JsonProperty("event")
    val event: Event? = null,

    @JsonProperty("type")
    val type: String = "list"
) : RichContentItem
