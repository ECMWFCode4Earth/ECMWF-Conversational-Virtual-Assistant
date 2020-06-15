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

    val bodyPlain: String,

    @Relationship("TITLE_QUESTION")
    val titleQuestion: QuestionAnswer?,

    val contentLength: Int,

    val createdDate: OffsetDateTime,

    val updatedDate: OffsetDateTime,

    val version: Int,

    @Relationship("LAST_EDIT_BY", direction = Relationship.UNDIRECTED)
    val updatedBy: ConfluenceAuthor,

    @Relationship("BELONGS_TO", direction = Relationship.UNDIRECTED)
    val space: ConfluenceSpace,

    @Relationship("EDITED_BY", direction = Relationship.UNDIRECTED)
    val edits: List<ConfluenceAuthor>?,

    @Relationship("FAQ")
    val faqs: List<QuestionAnswer>?,

    @Relationship("LABEL", direction = Relationship.UNDIRECTED)
    val labels: List<ConfluenceLabel>?,

    @Relationship("CHILD_PAGE")
    val childPage: ConfluencePage?,

    @Relationship("COMMENT", direction = Relationship.UNDIRECTED)
    val comments: List<ConfluenceComment>? ,

    @Relationship("INTERNAL_LINK")
    val internalLinks: List<ConfluencePage>? ,

    @Relationship("EXTERNAL_LINK")
    val externalLinks: List<WebLink>?

)



