package com._2horizon.cva.dialogflow.fulfillment.copernicus.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class Data(
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
    val parentrequestid: Any?,

    @JsonProperty("priority")
    val priority: Double,

    @JsonProperty("reasons")
    val reasons: List<Reason>,

    @JsonProperty("receivedat")
    val receivedat: String,

    @JsonProperty("requestid")
    val requestid: String,

    @JsonProperty("senttormqat")
    val senttormqat: String?,

    @JsonProperty("state")
    val state: String,

    @JsonProperty("subrequests")
    val subrequests: List<Subrequest>,

    @JsonProperty("username")
    val username: String
)
