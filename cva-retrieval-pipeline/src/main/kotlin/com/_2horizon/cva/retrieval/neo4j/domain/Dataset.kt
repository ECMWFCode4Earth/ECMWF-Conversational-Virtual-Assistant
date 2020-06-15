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
    val domains: List<DatasetDomain>?,

    @Relationship("PARAMETER_FAMILY", direction = Relationship.UNDIRECTED)
    val parameterFamilies: List<ParameterFamily>?,

    @Relationship("PRODUCT_TYPE")
    val productTypes: List<ProductType>?,

    @Relationship("DATASET_PROVIDER")
    val providers: List<DatasetProvider>?,

    @Relationship("SECTOR")
    val sectors: List<Sector>?,

    @Relationship("SPATIAL_COVERAGE")
    val spatialCoverages: List<SpatialCoverage>?,

    @Relationship("TEMPORAL_COVERAGE")
    val temporalCoverages: List<TemporalCoverage>?,

    @Relationship("TERMS")
    val terms: List<DatasetTerms>?,

    @Relationship("DOCUMENTED_BY")
    val docs: List<Documentation>?,

    @Relationship("RELATED_DATASETS")
    val relatedDatasets: List<Dataset>? = null,

    @Relationship("RELATED_APPLICATIONS")
    val relatedApplications: List<Application> = mutableListOf()

)
