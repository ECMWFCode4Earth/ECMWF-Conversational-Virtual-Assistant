package com._2horizon.cva.retrieval.event

import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com._2horizon.cva.retrieval.confluence.dto.space.Space
import com._2horizon.cva.retrieval.copernicus.Datastore
import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.nlp.SignificantTerm
import com._2horizon.cva.retrieval.sitemap.Sitemap
import twitter4j.Status

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
data class ConfluenceContentEvent( val spaceKey:String, val contentList: List<Content>)
data class ConfluenceSpacesEvent(val spacesList: List<Space>)
data class SitemapEvent(val sitemapsList: List<Sitemap>)
data class EcmwfPublicationsEvent(val ecmwfPublicationDTOs: List<EcmwfPublicationDTO>)
data class EcmwfPublicationsWithPdfContentEvent(val ecmwfPublicationDTOs: List<EcmwfPublicationDTO>)
data class CopernicusCatalogueReceivedEvent(val datastore:Datastore, val uiResources: List<UiResource>)
data class SignificantTermsReceivedEvent(val datastore: Datastore, val significantTerms: List<SignificantTerm>)


data class ConfluenceParentChildRelationshipEvent(val parentId:Long, val childId:Long)

data class TwitterBulkStatusEvent(val statuses: List<Status>)


