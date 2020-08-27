package com._2horizon.cva.common.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

data class View(
    @JsonProperty("representation")
    val representation: String,
    @JsonProperty("value")
    val value: String
) {
    val valueWithoutHtml: String
        get() = Jsoup.clean(value, Whitelist.none())
}
