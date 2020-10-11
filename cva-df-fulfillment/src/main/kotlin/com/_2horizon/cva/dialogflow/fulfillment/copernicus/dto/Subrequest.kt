package com._2horizon.cva.dialogflow.fulfillment.copernicus.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Subrequest(
    @JsonProperty("age")
    val age: Double,

    @JsonProperty("elapsed")
    val elapsed: String,

    @JsonProperty("inqueue")
    val inqueue: String,

    @JsonProperty("kind")
    val kind: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("origin")
    val origin: String,

    @JsonProperty("parentrequestid")
    val parentrequestid: String,

    @JsonProperty("priority")
    val priority: Double,

    @JsonProperty("receivedat")
    val receivedat: String,

    @JsonProperty("requestid")
    val requestid: String,

    @JsonProperty("senttormqat")
    val senttormqat: String,

    @JsonProperty("state")
    val state: String,

    @JsonProperty("username")
    val username: String
)
