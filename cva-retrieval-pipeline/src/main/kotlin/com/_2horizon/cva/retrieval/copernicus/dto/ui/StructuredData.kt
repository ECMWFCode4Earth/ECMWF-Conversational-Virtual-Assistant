package com._2horizon.cva.retrieval.copernicus.dto.ui


import com.fasterxml.jackson.annotation.JsonProperty

data class StructuredData(
    // @JsonProperty("@context")
    // val context: String,

    // @JsonProperty("creator")
    // val creator: Creator,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("distribution")
    val distribution: List<Distribution>,

    // @JsonProperty("includedInDataCatalog")
    // val includedInDataCatalog: IncludedInDataCatalog,

    // @JsonProperty("keywords")
    // val keywords: List<String>,

    // @JsonProperty("license")
    // val license: String,

    // @JsonProperty("logo")
    // val logo: String,

    // @JsonProperty("name")
    // val name: String,

    // @JsonProperty("sameAs")
    // val sameAs: String?,

    @JsonProperty("spatialCoverage")
    val spatialCoverage: SpatialCoverage,

    @JsonProperty("temporalCoverage")
    val temporalCoverage: String?

    // @JsonProperty("@type")
    // val type: String,
    
    // @JsonProperty("url")
    // val url: String
)
