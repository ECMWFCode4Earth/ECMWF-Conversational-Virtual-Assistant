package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.event.ConfluenceParentChildRelationshipEvent
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
@Singleton
class ConfluencePageChildrenRetriever(
    private val confluenceOperations:ConfluenceOperations ,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // fun retrievePageChildren(contentId:Int){
    //
    //     confluenceOperations.contentWithChildPages(contentId)
    //         .flatMapIterable { it.page.results }
    //         .flatMap { it.children }
    //
    //
    // }

    @EventListener
    fun onStartup(startupEvent: StartupEvent){
        // retrievePageChildren(174866096) // WIGOSWT
        // retrievePageChildren(55116796) // CKB
        retrievePageChildren(133257478) // CUSF
    }

    fun retrievePageChildren(parentId:Long){

        confluenceOperations.contentWithChildPages(parentId)
            .page.results
            .forEach {result ->

               val childId =  result.id

                applicationEventPublisher.publishEvent(ConfluenceParentChildRelationshipEvent(parentId = parentId, childId = childId))

                if (result.children!=null && result.children.page.results.isNotEmpty()){
                    retrievePageChildren(childId)
                }

            }


    }
}
