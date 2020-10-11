package com._2horizon.cva.dialogflow.manager.dialogflow

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import java.io.FileInputStream
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-16.
 */
@Factory
class DialogflowClientsHolderFactory(
    @Value("\${gcp.c3s.credentials}") private val c3sCredentialsLocation: String,
    @Value("\${gcp.cams.credentials}") private val camsCredentialsLocation: String,
    @Value("\${gcp.ecmwf.credentials}") private val ecmwfCredentialsLocation: String,

    @Value("\${gcp.c3s.project-id}") private val c3sProjectId: String,
    @Value("\${gcp.cams.project-id}") private val camsProjectId: String,
    @Value("\${gcp.ecmwf.project-id}") private val ecmwfProjectId: String,
) {
    @Singleton
    @Bean
    @Named("c3SDialogflowClientsHolder")
    fun c3SDialogflowClientsHolder(): DialogflowClientsHolder {
        return DialogflowClientsHolder(
            FixedCredentialsProvider.create(createGoogleCredentials(c3sCredentialsLocation)),
            c3sProjectId
        )
    }

    @Singleton
    @Bean
    @Named("camsDialogflowClientsHolder")
    fun camsDialogflowClientsHolder(): DialogflowClientsHolder {
        return DialogflowClientsHolder(
            FixedCredentialsProvider.create(createGoogleCredentials(camsCredentialsLocation)),
            camsProjectId
        )
    }

    @Singleton
    @Bean
    @Named("ecmwfDialogflowClientsHolder")
    fun ecmwfDialogflowClientsHolder(): DialogflowClientsHolder {
        return DialogflowClientsHolder(
            FixedCredentialsProvider.create(createGoogleCredentials(ecmwfCredentialsLocation)),
            ecmwfProjectId
        )
    }

    private fun createGoogleCredentials(credentialsLocation: String): GoogleCredentials =
        FileInputStream(credentialsLocation).use { fis ->
            GoogleCredentials.fromStream(fis)
        }
}
