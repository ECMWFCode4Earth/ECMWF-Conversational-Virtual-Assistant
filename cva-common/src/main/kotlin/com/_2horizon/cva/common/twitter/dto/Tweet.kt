package com._2horizon.cva.common.twitter.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-27.
 */
data class Tweet(
    val id: Long,
    val text: String,
    val source: String,
    val retweetId: Long?,

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: OffsetDateTime,
    val userId: Long,
    val userScreenName: String,
    val hashtags: List<String>,
    val urls: List<String>,
    val expandedUrls: List<String>,
    val mediaURLs: List<String>,
    val mediaExpandedUrls: List<String>,
)
