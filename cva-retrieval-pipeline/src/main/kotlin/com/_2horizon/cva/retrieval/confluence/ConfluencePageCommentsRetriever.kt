package com._2horizon.cva.retrieval.confluence

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
@Singleton
class ConfluencePageCommentsRetriever(
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
        
        // retrievePageComments(140380476)
    }

    fun retrievePageComments(pageId:Long){

       confluenceOperations.contentComments(pageId)
           .get()
           .contents
            .forEach {result ->

               val childId =  result.id

                // applicationEventPublisher.publishEvent(ConfluenceParentChildRelationshipEvent(parentId = parentId, childId = childId))
                //
                // if (result.children!=null && result.children.page.results.isNotEmpty()){
                //     retrievePageChildren(childId)
                // }

            }


    }
}
