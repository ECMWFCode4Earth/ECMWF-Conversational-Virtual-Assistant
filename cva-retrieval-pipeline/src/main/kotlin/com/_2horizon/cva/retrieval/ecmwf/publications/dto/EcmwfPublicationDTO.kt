package com._2horizon.cva.retrieval.ecmwf.publications.dto

/**
 * Created by Frank Lieber (liefra) on 2020-05-22.
 */
data class  EcmwfPublicationDTO(
    val nodeId:Int,
    val title:String,
    val contributors:List<String> = emptyList(),
    val keywords:List<String> = emptyList(),
    val abstract:String?,
    val number:String?,
    val secondaryTitle:String?,
    val tertiaryTitle:String?,
    val year:Int?,
    val pubDates:List<String> = emptyList(),
    val language:String?,
    val pages:String?,
    val issue:String?,
    val section:String?,
    val custom1:String?,
    val custom2:String?,
    val custom3:String?,
    val custom4:String?,
    val custom5:String?,
    val publicationType:String? = null ,
    val publicationPDF:String? = null
)
