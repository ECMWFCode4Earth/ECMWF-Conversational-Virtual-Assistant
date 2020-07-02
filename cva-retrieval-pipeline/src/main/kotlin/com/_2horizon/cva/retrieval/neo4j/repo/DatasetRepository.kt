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
        //TODO: check if only colon is included https://confluence.atlassian.com/confkb/the-differences-between-various-url-formats-for-a-confluence-page-278692715.html
        val query = "MATCH(p:ConfluencePage) WHERE replace(p.title, ':', '')=\$title AND p.spaceKey=\$spaceKey RETURN p"
        val params = mapOf(
            "title" to title.replace(":",""),
            "spaceKey" to spaceKey
        )
        val page= queryForObject<ConfluencePage>(query,params)
        return page
    }


}
