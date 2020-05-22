package com._2horizon.cva.retrieval.ecmwf.publications.dto

/**
 * Created by Frank Lieber (liefra) on 2020-05-22.
 */
data class  EcmwfPublicationDTO(
    val contributors:List<String>,
    val keywords:List<String>,
    val title:String,
    val abstract:String?,
    val number:String?,
    val secondaryTitle:String?,
    val tertiaryTitle:String?,
    val year:Int,
    val pubDates:List<String>,
    val language:String,
    val custom1:String?,
    val custom2:String?,
    val custom3:String?,
    val custom4:String?,
    val custom5:String?
)
