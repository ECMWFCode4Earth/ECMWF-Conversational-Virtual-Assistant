package com._2horizon.cva.common.dialogflow

import com._2horizon.cva.common.elastic.ContentSource

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
enum class Agent {
    C3S_CVA, CAMS_CVA, ECMWF_CVA
}

fun Agent.convertToContentSource(): ContentSource {
    return when (this) {
        Agent.C3S_CVA -> ContentSource.C3S
        Agent.CAMS_CVA -> ContentSource.CAMS
        else -> error("Couldn't convert agent $this to ContentSource")
    }
}
fun Agent.convertToTwitterUserScreenname(): String {
    return when (this) {
        Agent.C3S_CVA -> "CopernicusECMWF"
        Agent.CAMS_CVA -> "CopernicusECMWF"
        Agent.ECMWF_CVA -> "ECMWF"
    }
}
