package com._2horizon.cva.common.twitter.dto

import com._2horizon.cva.common.elastic.ContentSource
import com._2horizon.cva.common.elastic.ElasticBaseDTO
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-27.
 */
data class Tweet(
    override val id: String,
    override val source: ContentSource,
    override val content: String,
    @JsonProperty("dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    override val dateTime: LocalDateTime,

    val tweetId: Long,
    val tweetSource: String,
    val text: String,
    val retweetId: Long?,

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,
    val userId: Long,
    val userScreenName: String,
    val hashtags: List<String> = emptyList(),
    val urls: List<String> = emptyList(),
    val expandedUrls: List<String> = emptyList(),
    val mediaURLs: List<String> = emptyList(),
    val mediaExpandedUrls: List<String> = emptyList(),
) :ElasticBaseDTO
