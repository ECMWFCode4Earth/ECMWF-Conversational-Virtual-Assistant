package com._2horizon.cva.copernicus

import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import io.ino.solrs.JavaAsyncSolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocumentList
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.util.concurrent.CompletionStage
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-12.
 */
@Singleton
class CopernicusDataStoreAsyncSolrSearchService {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cdsClient: JavaAsyncSolrClient =
        JavaAsyncSolrClient.create("https://cds.climate.copernicus.eu/solr/drupal")

    private val adsClient: JavaAsyncSolrClient =
        JavaAsyncSolrClient.create("https://ads.atmosphere.copernicus.eu/solr/drupal")

    fun searchDatasetsByQueryTerm(term: String): Mono<List<CopernicusSolrResult>> {
        return search(term, "dataset")
    }

    fun searchApplicationsByQueryTerm(term: String): Mono<List<CopernicusSolrResult>> {
        return search(term, "application")
    }

    private fun search(term: String, type: String): Mono<List<CopernicusSolrResult>> {

        val query = SolrQuery("(gn_content:\"$term\")^100")
        query.addFilterQuery("ss_type:$type")
        query.highlight = true
        query.highlightSimplePre = "<mark>"
        query.highlightSimplePost = "<%2Fmark>"
        query.addHighlightField("gn_content")
        query.rows = 100

        val response: CompletionStage<QueryResponse> = cdsClient.query(query)

        return Mono.fromCompletionStage(response)
            .map { queryResponse ->

                val results: SolrDocumentList = queryResponse.results
                val highlighting = queryResponse.highlighting

                val docs: List<CopernicusSolrResult> = results.map { r ->
                    CopernicusSolrResult(
                        id = r["id"] as String,
                        title = r["ss_gn_title"] as String,
                        type = r["ss_type"] as String,
                        abstract = r["ss_gn_abstract"] as String,
                        keywords = r["sm_field_cds_keywords\$description"] as List<String>,
                        highlights = highlighting[r["id"] as String]!!["gn_content"] ?: emptyList()

                    )
                }

                docs

            }
    }
}
