package com._2horizon.cva.retrieval.neo4j.repo

import org.neo4j.driver.Driver
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-04-30.
 */
@Singleton
class DatasetRepository(
    driver: Driver,
    sessionFactory: SessionFactory
) : AbstractNeo4JRepository(driver, sessionFactory) {

    private val log = LoggerFactory.getLogger(javaClass)

    // fun findMovieAndTheirActors(movieName: String): List<Movie> {
    //     val query = """
    //         MATCH (m:Movie) <-[:ACTED_IN]-(p:Person) WHERE m.title =~ ".*${movieName}.*" RETURN m.title as title, collect(p.name) as actors
    //                   """.trimIndent()
    //
    //     return findMany(query) { r ->
    //         Movie(r.get("title").asString(), r.get("actors").asList { Actor(it.asString()) })
    //     }
    // }

    // fun saveMovie(movie: MovieEntity) {
    //
    //     val session = sessionFactory.openSession()
    //
    //     session.beginTransaction().use { tx ->
    //         try {
    //             session.save(movie)
    //             tx.commit()
    //         } catch (ex: Throwable) {
    //             log.error(ex.message)
    //             tx.rollback()
    //         }
    //     }
    //
    //     println("done saving movie")
    // }
}
