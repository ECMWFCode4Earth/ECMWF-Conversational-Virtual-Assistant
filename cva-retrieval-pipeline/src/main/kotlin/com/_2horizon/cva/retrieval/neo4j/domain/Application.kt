package com._2horizon.cva.retrieval.neo4j.domain

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.LocalDate

/**
 * Created by Frank Lieber (liefra) on 2020-06-01.
 */
@NodeEntity
data class Application(

    @Id
    val id: String,

    @Index
    val name: String,

    val title: String,

    val richAbstract: String,

    var downloadable: Boolean,

    var type: String,

    var lineage: String?,

    var publicationDate: LocalDate,

    @Relationship("DOMAIN", direction = Relationship.UNDIRECTED)
    val domains: List<DatasetDomain>?,

    @Relationship("PARAMETER_FAMILY", direction = Relationship.UNDIRECTED)
    val parameterFamilies: List<ParameterFamily>?,

    @Relationship("PRODUCT_TYPE", direction = Relationship.UNDIRECTED)
    val productTypes: List<ProductType>?,

    @Relationship("DATASET_PROVIDER", direction = Relationship.UNDIRECTED)
    val providers: List<DatasetProvider>?,

    @Relationship("SECTOR", direction = Relationship.UNDIRECTED)
    val sectors: List<Sector>?,

    @Relationship("SPATIAL_COVERAGE", direction = Relationship.UNDIRECTED)
    val spatialCoverages: List<SpatialCoverage>?,

    @Relationship("TEMPORAL_COVERAGE", direction = Relationship.UNDIRECTED)
    val temporalCoverages: List<TemporalCoverage>?,

    @Relationship("TERMS", direction = Relationship.UNDIRECTED)
    val terms: List<DatasetTerms>?,

    @Relationship("CONFLUENCE_PAGE")
    val confluencePages: List<ConfluencePage>?,

    @Relationship("CONFLUENCE_SPACE")
    val confluenceSpaces: List<ConfluenceSpace>?,

    @Relationship("EXTERNAL_LINK")
    val externalLinks: List<WebLink>?,

    @Relationship("RELATED_DATASETS")
    val relatedDatasets: List<Dataset>?,

    @Relationship("RELATED_APPLICATIONS")
    val relatedApplications: List<Application>?
)
