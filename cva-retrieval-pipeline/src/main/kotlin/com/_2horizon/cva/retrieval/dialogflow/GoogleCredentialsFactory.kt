package com._2horizon.cva.retrieval.dialogflow

import com.google.auth.oauth2.GoogleCredentials
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import java.io.FileInputStream
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-28.
 */
@Factory
@Requires(classes = [com.google.auth.oauth2.GoogleCredentials::class])
class GoogleCredentialsFactory(
    @Value("\${gcp.credentials.location}") val gcpCredentialsLocation: String
) {

    @Primary
    @Singleton
    fun defaultGoogleCredentials(): GoogleCredentials {
        val scopes: List<String> = emptyList()
        val fis = FileInputStream(gcpCredentialsLocation)
        val credentials: GoogleCredentials =  GoogleCredentials.fromStream(fis)
        fis.close()
        return credentials.createScoped(scopes)
    }
}
