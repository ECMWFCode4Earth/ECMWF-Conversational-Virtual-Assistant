package com._2horizon.cva.dialogflow.manager.dialogflow.copernicus

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.longrunning.OperationFuture
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.CreateDocumentRequest
import com.google.cloud.dialogflow.v2beta1.Document
import com.google.cloud.dialogflow.v2beta1.Document.KnowledgeType
import com.google.cloud.dialogflow.v2beta1.DocumentsClient
import com.google.cloud.dialogflow.v2beta1.DocumentsSettings
import com.google.cloud.dialogflow.v2beta1.KnowledgeBase
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.KnowledgeOperationMetadata
import com.google.cloud.dialogflow.v2beta1.ProjectName
import com.google.cloud.dialogflow.v2beta1.SessionsClient
import com.google.cloud.dialogflow.v2beta1.SessionsSettings
import com.google.protobuf.ByteString
import io.micronaut.gcp.GoogleCloudConfiguration
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-10.
 */
@Singleton
class CopernicusDialogflowKnowledgeBaseService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun addAllFaqDocuments() {
        val faqs = listOf(
            // DialogflowFAQ(
            //     "When does the information inserted in OSCAR/Surface (e.g., a new station.) show in the WIGOS Data Quality Monitoring System webtool?",
            //     "The WIGOS Data Quality Monitoring System webtool retrieves metadata information from OSCAR/Surface on a daily basis. For example, the availability maps are generated in near-real time by comparing the observations received by the NWP Centers against the schedules retrieved from OSCAR/Surface. Therefore, if the metadata has been updated today, tomorrow's maps should reflect that change. If a new station is added to OSCAR/Surface, it should appear on the WDQMS availability map on the next day. If that does not happen, it means that some fields may have not been correctly populated in OSCAR/Surface and this needs to be reported to the RWCs."
            // ) ,
            // DialogflowFAQ(
            //     "What is the cut off time in the different temporal categories (i.e, 6-hour, daily and alert)?",
            //     "The interval categories are defined as follows. The 6-hourly intervals are centred on the main synoptic hours: 00 (21 UTC ≤ t < 03 UTC); 06 (03 UTC ≤ t < 09 UTC); 12 (09 UTC ≤ t < 15 UTC); and 18  (15 UTC ≤ t < 21 UTC), where t refers to observation time. The daily interval is the union of the four 6-hourly intervals as defined above, therefore is the 24-hour period within the interval 21 UTC ≤ t < 21 UTC, where t is the observation time and the lower “21UTC” limit is the day before. The alert maps represent a 5-day moving average, therefore rely on daily values for a 5-day period."
            // )
            // ,
            // DialogflowFAQ(
            //     "Do Centers consider TAC or BUFR bulletins to calculate the statistics displayed on the web tool?",
            //     "The Centres take into account both TAC and BUFR, giving preference to the reports that were used in their assimilation systems. This means that the statistics computed for each station, interval and variable, will include mainly the observations assimilated (either TAC or BUFR) because observation duplicates are not considered in WDQMS. On the quality maps for surface observations, the 6-hourly intervals for a particular monitoring centre allow you to see the details of individual observations that contribute for the average value displayed on the map. This means that it is possible to check the O-B value, the usage (Status, i.e., used or not used because it was rejected by/before the assimilation) and the type of report (Type, i.e., TAC or BUFR) of a particular observation (identified by date and time) by hovering the mouse over each dot in the time series. For the upper-air, this information is provided on the 6-hourly availability maps for a single NWP Centre. Clicking on a dot on the map shows a pop-up with detailed information about the station data availability over the selected period. For the 6-hourly periods, details of the observations received such as Layer (Trop - from surface up 100hPa- and Stra -from 100hPa up balloon burst), Variable and observation type (TAC or the new high resolution BUFR reports) and status (used / not used, “used” meaning that at least one level/variable was assimilated) are provided."
            // )
            // ,
            // DialogflowFAQ(
            //     "What bulletin type does NCEP assimilate, TAC, BUFR or both? ",
            //     "NCEP does not provide this information to WDQMS (that is why it is shown as Type: n/a) as they don't currently pass info into their DA database on whether the observation source was TAC or BUFR. However, most data NCEP uses is TAC."
            // )
            DialogflowFAQ(
                "WIGOS-FAQ",
                FileReader("/Users/liefra/Downloads/Table 1-Grid view (1).csv").use { it.readText() }
            )
        )

        val knowledgeBase = findKnowledgeBaseByDisplayName("WIGOS")

        faqs.forEach { faq ->

            addFaqDocument(
                knowledgeBase.name,
                faq.question,
                faq.answer
            )

        }
    }

    fun addAllCKBDocuments() {

        val knowledgeBase = findKnowledgeBaseByDisplayName("CKB")

        val pages = listOf(
            ConfluencePage("How to install and use CDS API on Windows", 121847376),
            ConfluencePage("How to install and use CDS API on macOS", 140380488)
        )

        pages.forEach { page ->

            val pageSrc = Jsoup.parse(
                URL("https://confluence.ecmwf.int/plugins/viewsource/viewpagesrc.action?pageId=${page.pageId}"),
                60000
            )
            val bodyHtml = pageSrc.body().html()
            val allHtml = """
<h1>${page.title}</h1>
$bodyHtml               
           """.trimIndent()

            addExtractiveQaDocument(knowledgeBase.name, page.title, allHtml)
        }
    }

    fun addExtractiveQaDocument(
        knowledgeBaseName: String,
        displayName: String,
        rawContent: String
    ): Document {
        return createDocument(
            knowledgeBaseName,
            displayName,
            "text/html",
            KnowledgeType.EXTRACTIVE_QA,
            rawContent
        )
    }

    fun addFaqDocument(
        knowledgeBaseName: String,
        displayName: String,
        rawContent: String
    ): Document {
        return createDocument(
            knowledgeBaseName,
            displayName,
            "text/csv",
            KnowledgeType.FAQ,
            rawContent
        )
    }

    fun createDocument(
        knowledgeBaseName: String,
        displayName: String,
        mimeType: String,
        knowledgeType: KnowledgeType,
        rawContent: String
    ): Document {
        getDocumentsClient().use { documentsClient ->

            val document = Document.newBuilder()
                .setDisplayName(displayName)
                // .setContentUri(contentUri)
                .setMimeType(mimeType)
                .setRawContent(ByteString.copyFrom(rawContent, Charset.defaultCharset()))
                .addKnowledgeTypes(knowledgeType)
                .build()

            val createDocumentRequest: CreateDocumentRequest = CreateDocumentRequest.newBuilder()
                .setDocument(document)
                .setParent(knowledgeBaseName)
                .build()

            val response: OperationFuture<Document, KnowledgeOperationMetadata> =
                documentsClient.createDocumentAsync(createDocumentRequest)
            val createdDocument: Document = response[180, TimeUnit.SECONDS]
            System.out.format("Created Document:\n")
            System.out.format(" - Display Name: %s\n", createdDocument.displayName)
            System.out.format(" - Knowledge ID: %s\n", createdDocument.name)
            System.out.format(" - MIME Type: %s\n", createdDocument.mimeType)
            System.out.format(" - Knowledge Types:\n")
            for (knowledgeTypeId in document.knowledgeTypesList) {
                System.out.format("  - %s \n", knowledgeTypeId.valueDescriptor)
            }
            System.out.format(" - Source: %s \n", document.contentUri)
            return createdDocument

        }
    }

    fun findKnowledgeBaseByDisplayName(displayName: String): KnowledgeBase {
        getKnowledgeBasesClient().use { knowledgeBasesClient ->
            return knowledgeBasesClient.listKnowledgeBases(ProjectName.of(googleCloudConfiguration.projectId))
                .iterateAll().first { it.displayName == displayName }
        }
    }

    fun createKnowledgeBase(displayName: String): KnowledgeBase {
        getKnowledgeBasesClient().use { knowledgeBasesClient ->
            val knowledgeBase = KnowledgeBase.newBuilder().setDisplayName(displayName).build()
            val projectName = ProjectName.of(googleCloudConfiguration.projectId)
            return knowledgeBasesClient.createKnowledgeBase(projectName, knowledgeBase)
        }
    }

    private fun getDocumentsClient() =
        DocumentsClient.create(
            DocumentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getKnowledgeBasesClient() =
        KnowledgeBasesClient.create(
            KnowledgeBasesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getSessionsClient() =
        SessionsClient.create(SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())
}

data class ConfluencePage(
    val title: String,
    val pageId: Int
)

data class DialogflowFAQ(
    val question: String,
    val answer: String
)
