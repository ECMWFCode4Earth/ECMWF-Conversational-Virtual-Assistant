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
    val domains: Set<DatasetDomain> = mutableSetOf(),

    @Relationship("PARAMETER_FAMILY")
    val parameterFamilies: Set<ParameterFamily> = mutableSetOf(),

    @Relationship("PRODUCT_TYPE")
    val productTypes: Set<ProductType> = mutableSetOf(),

    @Relationship("DATASET_PROVIDER")
    val providers: Set<DatasetProvider> = mutableSetOf(),

    @Relationship("SECTOR")
    val sectors: Set<Sector> = mutableSetOf(),

    @Relationship("SPATIAL_COVERAGE")
    val spatialCoverages: Set<SpatialCoverage> = mutableSetOf(),

    @Relationship("TEMPORAL_COVERAGE")
    val temporalCoverages: Set<TemporalCoverage> = mutableSetOf(),

    @Relationship("TERMS")
    val terms: Set<DatasetTerms> = mutableSetOf(),

    @Relationship("DOCUMENTED_BY")
    val docs: Set<Documentation> = mutableSetOf(),

    @Relationship("RELATED_DATASETS")
    val relatedDatasets: Set<Dataset> = mutableSetOf()
)
