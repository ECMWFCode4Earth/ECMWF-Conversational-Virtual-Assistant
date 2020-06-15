package com._2horizon.cva.retrieval.neo4j.domain

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
@NodeEntity
data class ConfluenceSpace(

    @Id
    val spaceKey: String,

    @Index
    val spaceId: Long,

    @Index
    val name: String,

    @Index
    val type: String,

    val description: String,

    @Relationship("LABEL")
    val labels: List<ConfluenceLabel>?

)
