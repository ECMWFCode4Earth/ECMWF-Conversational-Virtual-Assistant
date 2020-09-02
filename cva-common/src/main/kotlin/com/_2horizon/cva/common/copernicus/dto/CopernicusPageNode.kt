package com._2horizon.cva.common.copernicus.dto

import com._2horizon.cva.common.elastic.ContentSource
import com._2horizon.cva.common.elastic.ElasticBaseDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
data class CopernicusPageNode(
    override val id: String,
    override val source: ContentSource,
    override val content: String,

    @JsonProperty("dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    override val dateTime: LocalDateTime,

    @JsonProperty("verifiedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    override val verifiedAt: LocalDateTime,

    val url: String,
    val title: String,
    val nodeType: NodeType,

    val img: String? = null,

    @JsonProperty("publishedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val publishedAt: LocalDate? = null,

    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,

    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,

    val teaser: String? = null,
    val contentHtml: String? = null,
    val contentStripped: String? = null,
) : ElasticBaseDTO


