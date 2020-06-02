package com._2horizon.cva.retrieval.neo4j.repo

import org.neo4j.driver.Driver
import org.neo4j.driver.Record
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import smile.data.Dataset
import java.lang.reflect.Parameter

/**
 * Created by Frank Lieber (liefra) on 2020-04-30.
 */
abstract class AbstractNeo4JRepository(
    protected val driver: Driver,
    protected val sessionFactory: SessionFactory
) {

    private val log = LoggerFactory.getLogger(javaClass)

    internal fun <R> findOne(query: String, parameters: Map<String,Any> = emptyMap(), transform: (Record) -> R): R? {
        return driver.session().use { s ->
            s.readTransaction { tx ->
                tx.run(query,parameters).list(transform).firstOrNull()
            }
        }
    }

    internal fun <R> findMany(query: String, parameters: Map<String,Any> = emptyMap(), transform: (Record) -> R): List<R> {
        return driver.session().use { s ->
            s.readTransaction { tx ->
                tx.run(query,parameters).list(transform)
            }
        }
    }

    internal inline fun <reified R> load(id:String): R {
        return sessionFactory.openSession().load(R::class.java,id)
    }

    fun <R> save(o: R, depth:Int = 1) {

        val session = sessionFactory.openSession()

        session.beginTransaction().use { tx ->
            try {
                session.save(o, depth)
                tx.commit()
            } catch (ex: Throwable) {
                log.error(ex.message)
                tx.rollback()
            }
        }
    }
}
