package com._2horizon.cva.dialogflow.fulfillment.analytics

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.dialogflow.v2beta1.Context
import java.time.LocalDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-08-23.
 */
data class DialogflowConversionStep(
    @JsonProperty("dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val dateTime: LocalDateTime,
    val session: String,
    val action: String,
    val agent: String,
    val responseId: String,
    val queryText: String,
    val alternativeQueryResultsCount: Int,
    val intentName: String,
    val intentDisplayName: String,
    val intentDetectionConfidence: Float,

    @JsonIgnore
    val outputContextsList: List<Context>,

    @JsonIgnore
    val parameters: Map<String, String> = emptyMap(),

    val outputContexts: List<String> = emptyList(),
    val parameterKeys: List<String> = emptyList(),
    val parameterValues: List<String> = emptyList(),
)
