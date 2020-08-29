package com._2horizon.cva.common.elastic

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
interface ElasticBaseDTO {
    val id: String
    val source: ContentSource
}

enum class ContentSource{
    TWITTER, C3S,CAMS
}
