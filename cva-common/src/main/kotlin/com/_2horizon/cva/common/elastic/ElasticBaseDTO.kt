package com._2horizon.cva.common.elastic

import java.time.LocalDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
interface ElasticBaseDTO {
    val id: String
    val source: ContentSource
    val content: String
    val date: LocalDateTime
}

enum class ContentSource{
    TWITTER, C3S,CAMS
}
