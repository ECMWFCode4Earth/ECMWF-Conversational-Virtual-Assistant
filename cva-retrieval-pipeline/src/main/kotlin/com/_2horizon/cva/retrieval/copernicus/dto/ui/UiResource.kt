package com._2horizon.cva.retrieval.copernicus.dto.ui


import com._2horizon.cva.retrieval.copernicus.dto.WmsSample
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import java.time.LocalDate

data class UiResource(

    @JsonProperty("form")
    val formNode: JsonNode,
    //
    // @JsonProperty("form_source")
    // val formSource: Any?,
    //
    @JsonProperty("constraints")
    val constraints: JsonNode,
    //
    // @JsonProperty("constraints_source")
    // val constraintsSource: Any?,

    @JsonProperty("method")
    val method: String,

    @JsonProperty("selection_limit")
    val selectionLimit: Int,

    // @JsonProperty("errors")
    // val errors: List<Any>,

    @JsonProperty("toolbox_compat")
    val toolboxCompat: Boolean,

    @JsonProperty("downloadable")
    val downloadable: Boolean,

    @JsonProperty("responsible_individual")
    val responsibleIndividual: String?,   

    @JsonProperty("responsible_organisation")
    val responsibleOrganisation: String?,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("purpose")
    val purpose: String?,

    @JsonProperty("publication_date")
    val publicationDate: LocalDate,

    @JsonProperty("lineage")
    val lineage: String?,

    @JsonProperty("contact_email")
    val contactEmail: String,

    @JsonProperty("cds_keywords")
    val cdsKeywords: List<String>,

    @JsonProperty("external_links")
    val externalLinks: List<ExternalLink>,

    @JsonProperty("preview_image_url")
    val previewImageUrl: String?,

    // @JsonProperty("coster_size_multiplier")
    // val costerSizeMultiplier: Any?,

    // @JsonProperty("coster_time_multiplier")
    // val costerTimeMultiplier: Any?,

    @JsonProperty("doi")
    val doi: String?,

    @JsonProperty("structured_data")
    val structuredData: StructuredData,

    // @JsonProperty("configuration")
    // val configuration: Any?,

    @JsonProperty("related_resource")
    val relatedResource: String?,

    @JsonProperty("eqc_enabled")
    val eqcEnabled: Boolean,

    // @JsonProperty("user_defined_widgets")
    // val userDefinedWidgets: List<Any>,

    // @JsonProperty("reference_block")
    // val referenceBlock: List<Any>,


    @JsonProperty("related_resources")
    val relatedResources: List<RelatedResource>,

    @JsonProperty("slug")
    val slug: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("id")
    val id: String,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("rich_abstract")
    val richAbstract: String,
    
    @JsonProperty("terms")
    val terms: List<String>,
    
    @JsonProperty("wms_sample")
    val wmsSample: WmsSample?
)    {
    val richAbstractCleaned: String
        get() = Jsoup.clean(richAbstract, Whitelist.none())
}
