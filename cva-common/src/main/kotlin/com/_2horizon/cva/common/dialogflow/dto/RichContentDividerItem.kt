package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentDividerItem(
    @JsonProperty("type")
    override val type: String = "divider"
) : RichContentItem
