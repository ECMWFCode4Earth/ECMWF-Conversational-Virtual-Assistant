// package com._2horizon.cva.retrieval.neo4j.domain
//
// import org.neo4j.ogm.annotation.EndNode
// import org.neo4j.ogm.annotation.GeneratedValue
// import org.neo4j.ogm.annotation.Id
// import org.neo4j.ogm.annotation.Property
// import org.neo4j.ogm.annotation.RelationshipEntity
// import org.neo4j.ogm.annotation.StartNode
// import java.time.OffsetDateTime
//
// /**
//  * Created by Frank Lieber (liefra) on 2020-06-11.
//  */
// @RelationshipEntity("EDITED_BY")
// data class ConfluenceEdit(
//
//     @Id @GeneratedValue
//     val relationshipId: Long?=null,
//
//     @StartNode
//     val page: ConfluencePage,
//
//     @EndNode
//     val editor: ConfluenceAuthor,
//
//     @Property
//     val version: Int,
//
//     @Property
//     val datetime: OffsetDateTime
//
// )
