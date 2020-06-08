package com._2horizon.cva.retrieval.neo4j.domain

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity

/**
 * Created by Frank Lieber (liefra) on 2020-06-01.
 */

@NodeEntity
class ConfluenceAuthor(
    @Id
    val userKey: String,

    val name: String,

    val displayName: String,

    @Index
    val type: String

)
