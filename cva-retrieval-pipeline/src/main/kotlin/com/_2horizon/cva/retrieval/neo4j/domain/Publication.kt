package com._2horizon.cva.retrieval.neo4j.domain

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.LocalDate

/**
 * Created by Frank Lieber (liefra) on 2020-06-01.
 */
@NodeEntity
data class Publication(

    @Id
    val nodeId: String,

    @Index
    val title: String,

    val abstract:String?,
    val number:String?,
    val secondaryTitle:String?,
    val tertiaryTitle:String?,
    val year:Int?,
    val pubDate:LocalDate?,
    val language:String?,
    val pages:String?,
    val issue:String?,
    val section:String?,
    val custom1:String?,
    val custom2:String?,

    @Relationship("KEYWORD", direction = Relationship.UNDIRECTED)
    val keywords: List<PublicationKeyword>?,

    @Relationship("CONTRIBUTOR", direction = Relationship.UNDIRECTED)
    val publicationContributors: List<PublicationContributor>? ,

    @Relationship("CONFLUENCE_PAGE")
    val confluencePages: List<ConfluencePage>? ,

    @Relationship("CONFLUENCE_SPACE")
    val confluenceSpaces: List<ConfluenceSpace>? ,

    @Relationship("EXTERNAL_LINK")
    val externalLinks: List<WebLink>?



)
