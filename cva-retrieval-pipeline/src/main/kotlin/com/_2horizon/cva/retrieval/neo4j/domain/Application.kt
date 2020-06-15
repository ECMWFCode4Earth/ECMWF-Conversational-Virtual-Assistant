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

    var downloadable: Boolean,

    var type: String,

    var lineage: String?,

    var publicationDate: LocalDate,

    @Relationship("DOMAIN")
    val domains: List<DatasetDomain> = mutableListOf(),

    @Relationship("PARAMETER_FAMILY")
    val parameterFamilies: List<ParameterFamily> = mutableListOf(),

    @Relationship("PRODUCT_TYPE")
    val productTypes: List<ProductType> = mutableListOf(),

    @Relationship("DATASET_PROVIDER")
    val providers: List<DatasetProvider> = mutableListOf(),

    @Relationship("SECTOR")
    val sectors: List<Sector> = mutableListOf(),

    @Relationship("SPATIAL_COVERAGE")
    val spatialCoverages: List<SpatialCoverage> = mutableListOf(),

    @Relationship("TEMPORAL_COVERAGE")
    val temporalCoverages: List<TemporalCoverage> = mutableListOf(),

    @Relationship("TERMS")
    val terms: List<DatasetTerms> = mutableListOf(),

    @Relationship("DOCUMENTED_BY")
    val docs: List<Documentation> = mutableListOf(),

    @Relationship("RELATED_DATASETS")
    val relatedDatasets: List<Dataset> = mutableListOf(),

    @Relationship("RELATED_APPLICATIONS")
    val relatedApplications: List<Application> = mutableListOf()
)
