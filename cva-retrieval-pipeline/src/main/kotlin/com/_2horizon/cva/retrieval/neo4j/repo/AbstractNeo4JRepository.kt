package com._2horizon.cva.retrieval.neo4j.repo

import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory

/**
 * Created by Frank Lieber (liefra) on 2020-04-30.
 */
abstract class AbstractNeo4JRepository(
    // protected val driver: Driver,
    protected val sessionFactory: SessionFactory
) {

    private val log = LoggerFactory.getLogger(javaClass)

    // internal fun <R> findOne(query: String, parameters: Map<String,Any> = emptyMap(), transform: (Record) -> R): R? {
    //     return driver.session().use { s ->
    //         s.readTransaction { tx ->
    //             tx.run(query,parameters).list(transform).firstOrNull()
    //         }
    //     }
    // }
    //
    // internal fun <R> findMany(query: String, parameters: Map<String,Any> = emptyMap(), transform: (Record) -> R): List<R> {
    //     return driver.session().use { s ->
    //         s.readTransaction { tx ->
    //             tx.run(query,parameters).list(transform)
    //         }
    //     }
    // }

    internal inline fun <reified R> loadOrNull(id: String): R? {
        return sessionFactory.openSession().load(R::class.java, id)
    }

    internal inline fun <reified R> load(id: String): R {
        val result = sessionFactory.openSession().load(R::class.java, id)
        check(result != null) { "Couldn't load $id of type ${R::class.java}" }
        return result
    }

    internal inline fun <reified R> queryForObject(cypher: String, parameters: Map<String, Any>): R? {
        return try {
            sessionFactory.openSession().queryForObject(R::class.java, cypher, parameters)
        } catch (ex: Throwable) {
            log.warn("Error in load ${ex.message}")
            null
        }
    }

    fun <R> save(o: R, depth: Int = 1) {

        val session = sessionFactory.openSession()

        session.beginTransaction().use { tx ->
            try {
                session.save(o, depth)
                tx.commit()
            } catch (ex: Throwable) {
                log.error(ex.message)
                tx.rollback()
                throw ex
            }
        }
    }
}
