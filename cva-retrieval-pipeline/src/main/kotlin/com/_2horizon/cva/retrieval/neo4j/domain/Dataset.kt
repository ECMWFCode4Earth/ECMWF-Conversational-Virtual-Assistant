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
data class Dataset(

    @Id
    val id: String,

    @Index
    val name: String,

    val title: String,

    val downloadable: Boolean,

    val type: String,

    @Relationship("LINEAGE")
    val lineage: Lineage?,

    val publicationDate: LocalDate,

    @Relationship("DOMAIN")
    val domains: Set<DatasetDomain>?,

    @Relationship("PARAMETER_FAMILY", direction = Relationship.UNDIRECTED)
    val parameterFamilies: Set<ParameterFamily>?,

    @Relationship("PRODUCT_TYPE")
    val productTypes: Set<ProductType>?,

    @Relationship("DATASET_PROVIDER")
    val providers: Set<DatasetProvider>?,

    @Relationship("SECTOR")
    val sectors: Set<Sector>?,

    @Relationship("SPATIAL_COVERAGE")
    val spatialCoverages: Set<SpatialCoverage>?,

    @Relationship("TEMPORAL_COVERAGE")
    val temporalCoverages: Set<TemporalCoverage>?,

    @Relationship("TERMS")
    val terms: Set<DatasetTerms>?,

    @Relationship("DOCUMENTED_BY")
    val docs: Set<Documentation>?,

    @Relationship("RELATED_DATASETS")
    val relatedDatasets: Set<Dataset>? = null,

    @Relationship("RELATED_APPLICATIONS")
    val relatedApplications: Set<Application> = mutableSetOf()

)
