package com._2horizon.cva.copernicus

import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.common.SolrDocumentList
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-12.
 */
@Singleton
class CopernicusDataStoreSolrSearchService {

    private val log = LoggerFactory.getLogger(javaClass)

    private val cdsClient: HttpSolrClient = HttpSolrClient.Builder("https://cds.climate.copernicus.eu/solr")
        .withConnectionTimeout(10000)
        .withSocketTimeout(60000)
        .build()

    private val adsClient: HttpSolrClient = HttpSolrClient.Builder("https://ads.atmosphere.copernicus.eu/solr")
        .withConnectionTimeout(10000)
        .withSocketTimeout(60000)
        .build()

    fun searchDatasetsByQueryTerm(term :String) : List<CopernicusSolrResult>{
         return search(term, "dataset")
    }

    fun searchApplicationsByQueryTerm(term :String) : List<CopernicusSolrResult>{
        return search(term, "application")
    }

    private fun search(term :String, type:String) : List<CopernicusSolrResult>{

        return try {
            val query = SolrQuery("(gn_content:\"$term\")^100")
            query.addFilterQuery("ss_type:$type")
            query.highlight = true
            query.highlightSimplePre = "<mark>"
            query.highlightSimplePost = "<%2Fmark>"
            query.addHighlightField("gn_content")
            query.rows = 100

            val response = cdsClient.query("drupal", query)
            val results: SolrDocumentList = response.results
            val highlighting = response.highlighting

            val docs: List<CopernicusSolrResult> = results.map { r ->
                CopernicusSolrResult(
                    id = r["id"] as String,
                    title = r["ss_gn_title"] as String,
                    type = r["ss_type"] as String  ,
                    abstract = r["ss_gn_abstract"] as String  ,
                    keywords = r["sm_field_cds_keywords\$description"] as List<String>,
                    highlights =  highlighting[r["id"] as String]!!["gn_content"] ?: emptyList()

                )
            }

            docs

        }   catch (ex:Throwable){
            log.warn("Solr search error ${ex.message}")
            emptyList()
        }

    }
}
