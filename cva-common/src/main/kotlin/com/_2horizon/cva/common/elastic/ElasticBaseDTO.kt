package com._2horizon.cva.common.elastic

import java.time.LocalDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
interface ElasticBaseDTO {
    val id: String
    val source: ContentSource
    val content: String
    val dateTime: LocalDateTime
    val verifiedAt: LocalDateTime
}

enum class ContentSource {
    TWITTER, C3S, CAMS
}

fun ContentSource.baseUri(): String {
    return when (this) {
        ContentSource.C3S -> "https://climate.copernicus.eu"
        ContentSource.TWITTER -> "https://twitter.com/CivGame/status"
        ContentSource.CAMS -> "https://atmosphere.copernicus.eu"
    }
}
