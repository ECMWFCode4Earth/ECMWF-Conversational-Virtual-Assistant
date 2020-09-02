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
data class CopernicusEventNode(
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

    val location: String,

    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val startDate: LocalDate,

    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

    val teaser: String,
    val nodeType: NodeType,
    val contentHtml: String? = null,
    val contentStripped: String? = null,
) : ElasticBaseDTO


