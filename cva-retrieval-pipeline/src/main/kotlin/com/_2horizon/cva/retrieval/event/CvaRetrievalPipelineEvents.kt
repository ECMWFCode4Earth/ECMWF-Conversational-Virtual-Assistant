package com._2horizon.cva.retrieval.event

import com._2horizon.cva.common.confluence.dto.content.Content
import com._2horizon.cva.common.confluence.dto.space.Space
import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.ContentSource
import com._2horizon.cva.common.twitter.dto.Tweet
import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import com._2horizon.cva.retrieval.copernicus.Datastore
import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.sitemap.Sitemap

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
data class ConfluenceContentEvent(val spaceKey: String, val contentList: List<Content>)
data class ConfluenceSpacesEvent(val spacesList: List<Space>)
data class SitemapEvent(val sitemapsList: List<Sitemap>)
data class EcmwfPublicationsEvent(val ecmwfPublicationDTOs: List<EcmwfPublicationDTO>)
data class EcmwfPublicationsWithPdfContentEvent(val ecmwfPublicationDTOs: List<EcmwfPublicationDTO>)
data class CopernicusCatalogueReceivedEvent(val datastore: Datastore, val results: List<CopernicusSolrResult>)
data class ConfluenceParentChildRelationshipEvent(val parentId: Long, val childId: Long)
data class TwitterBulkStatusEvent(val tweets: List<Tweet>)
data class BasicPageNodesEvent(
    val contentSource: ContentSource,
    val nodeType: NodeType,
    val itemCopernicuses: List<CopernicusPageNode>
)

data class ContentPageNodesEvent(
    val contentSource: ContentSource,
    val nodeType: NodeType,
    val itemCopernicuses: List<CopernicusPageNode>
)


