package com._2horizon.cva.retrieval.neo4j.domain

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.OffsetDateTime

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
@NodeEntity
data class ConfluencePage(

    @Id
    val contentId: String,

    @Index
    val spaceKey: String,

    val title: String,

    @Index
    val type: String,

    @Index
    val status: String,

    val createdDate: OffsetDateTime,

    val updatedDate: OffsetDateTime,

    val version: Int,

    @Relationship("LAST_UPDATE_BY")
    val updatedBy: ConfluenceAuthor,

    @Relationship("AUTHORED_BY")
    val authors: Set<ConfluenceAuthor>?,

    @Relationship("LABEL")
    val labels: Set<ConfluenceLabel>?,

    @Relationship("CHILD_PAGE")
    val childPage: ConfluencePage?

)



