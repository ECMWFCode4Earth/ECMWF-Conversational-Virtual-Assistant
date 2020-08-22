package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentSuggestionChipsItem(

    @JsonProperty("options")
    val options: List<ChipOption>,

    @JsonProperty("type")
    val type: String = "chips"
) : RichContentItem {
    data class ChipOption(
        @JsonProperty("text")
        val text: String,

        @JsonProperty("link")
        val link: String,

        @JsonProperty("image")
        val image: ChipImage? = null
    ) {
        data class ChipImage(
            @JsonProperty("src")
            val src: ChipImageSrc
        ) {
            data class ChipImageSrc(
                @JsonProperty("rawUrl")
                val rawUrl: String
            )
        }
    }
}


