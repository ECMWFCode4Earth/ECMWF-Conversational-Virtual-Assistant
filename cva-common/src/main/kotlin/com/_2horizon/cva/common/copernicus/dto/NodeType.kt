package com._2horizon.cva.common.copernicus.dto

/**
 * Created by Frank Lieber (liefra) on 2020-08-29.
 */
enum class NodeType {
    NEWS, EVENT, PRESS_RELEASE, CASE_STUDY, DEMONSTRATOR_PROJECT
}

fun NodeType.asHumanReadable(): String {
    return when (this) {
        NodeType.NEWS -> "News"
        NodeType.EVENT -> "Event"
        NodeType.PRESS_RELEASE -> "Press Release"
        NodeType.CASE_STUDY -> "Case Study"
        NodeType.DEMONSTRATOR_PROJECT -> "Demonstrator Project"
    }
}
