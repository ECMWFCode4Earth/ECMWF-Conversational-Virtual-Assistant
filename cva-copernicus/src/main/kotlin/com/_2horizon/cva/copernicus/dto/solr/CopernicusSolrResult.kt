package com._2horizon.cva.copernicus.dto.solr

/**
 * Created by Frank Lieber (liefra) on 2020-07-12.
 */
data class CopernicusSolrResult(
    val id: String,
    val type: String,
    val abstract: String,
    val keywords: List<String>,
    val title: String,
    val highlights: List<String>
)
