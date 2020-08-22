package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentDividerItem(
    @JsonProperty("type")
    val type: String = "divider"
) : RichContentItem
