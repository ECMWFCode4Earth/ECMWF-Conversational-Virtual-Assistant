package com._2horizon.cva.retrieval.neo4j.repo

import com._2horizon.cva.retrieval.neo4j.domain.ConfluencePage
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-04-30.
 */
@Singleton
class DatasetRepository(
    // driver: Driver,
    sessionFactory: SessionFactory
) : AbstractNeo4JRepository( sessionFactory) {

    private val log = LoggerFactory.getLogger(javaClass)



    fun findConfluencePageByTitleAndSpaceKey(title: String,spaceKey:String): ConfluencePage? {
        val query = "MATCH(p:ConfluencePage) WHERE p.title='\$title' AND p.spaceKey='\$spaceKey' RETURN p"
        val params = mapOf(
            "title" to title,
            "spaceKey" to spaceKey
        )
        return queryForObject<ConfluencePage>(query,params)
    }


}
