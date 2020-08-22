package com._2horizon.cva.dialogflow.fulfillment.copernicus.dto


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class CopernicusDataStoreStatus(
    // @JsonProperty("data")
    // val `data`: List<Data>,
    //
    // @JsonProperty("priority_range")
    // val priorityRange: List<Double>,

    @JsonProperty("queued")
    val queued: Int,

    @JsonProperty("queued_users")
    val queuedUsers: Int,

    @JsonProperty("running")
    val running: Int,

    @JsonProperty("running_users")
    val runningUsers: Int,

    @JsonProperty("timestamp")
    val timestamp: OffsetDateTime,

    @JsonProperty("total_users")
    val totalUsers: Int,

    @JsonProperty("user")
    val user: String?
)
