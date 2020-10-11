import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val airtableVersion: String by project
val jsoupVersion: String by project
val commonsIoVersion: String by project
val pdfBoxVersion: String by project
val elasticsearchRestVersion: String by project
val elasticsearchBeyonderVersion: String by project
val micronautNeo4jBoltVersion: String by project
val micronautNeo4jOgmVersion: String by project
val reactorVersion: String by project
val coreNlpKtVersion: String by project
val openNlpVersion: String by project
val dialogflowVersion: String by project
val micronautGcpVersion: String by project
val commonCsvVersion: String by project
val micronautReactorVersion: String by project
val twitter4jVersion: String by project
val googleCloudBomVersion: String by project


plugins {
    groovy
    application
}

version = "0.1"

application {
    mainClassName = "com._2horizon.cva.retrieval.Application"
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {

    implementation(project(":cva-common"))
    implementation(project(":cva-airtable"))
    implementation(project(":cva-copernicus"))

    // https://cloud.google.com/dialogflow/docs/reference/libraries/java
    implementation(platform("com.google.cloud:libraries-bom:$googleCloudBomVersion"))
    implementation("com.google.cloud:google-cloud-dialogflow")
    implementation("io.micronaut.gcp:micronaut-gcp-common:$micronautGcpVersion")

    // https://jsoup.org/
    implementation("org.jsoup:jsoup:$jsoupVersion")

    // http://commons.apache.org/proper/commons-io/
    implementation("commons-io:commons-io:$commonsIoVersion")

    // https://pdfbox.apache.org/
    implementation("org.apache.pdfbox:pdfbox:$pdfBoxVersion")

    // elasticsearch
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:$elasticsearchRestVersion")
    implementation("fr.pilato.elasticsearch:elasticsearch-beyonder:$elasticsearchBeyonderVersion")

    // https://commons.apache.org/proper/commons-csv/
    implementation("org.apache.commons:commons-csv:$commonCsvVersion")

    //https://micronaut-projects.github.io/micronaut-reactor/latest/guide/
    implementation("io.micronaut.reactor:micronaut-reactor:$micronautReactorVersion")

    // http://twitter4j.org/en/
    implementation("org.twitter4j:twitter4j-core:$twitter4jVersion")

    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut.graphql:micronaut-graphql")
    kapt("io.micronaut.configuration:micronaut-openapi")

}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    withType<Test> {
        useJUnitPlatform()
        maxHeapSize = "4G" // Needed for CoreNLP NER
    }
    withType<ShadowJar> {
        mergeServiceFiles()
    }
    withType<JavaExec> {
        classpath += configurations.getByName("developmentOnly")
        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }
}

noArg {
    annotation("org.neo4j.ogm.annotation.NodeEntity")
    annotation("org.neo4j.ogm.annotation.RelationshipEntity")
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

tasks.create(name = "deploy-cva-retrieval-pipeline") {

    dependsOn("assemble")

    group = "deploy"

    val myServer = org.hidetake.groovy.ssh.core.Remote(
        mapOf<String, Any>(
            "host" to "manage-cva.ecmwf.int",
            "user" to "esowc25",
            "identity" to File("~/.ssh/id_rsa")
        )
    )

    doLast {
        ssh.run(delegateClosureOf<org.hidetake.groovy.ssh.core.RunHandler> {
            session(myServer, delegateClosureOf<org.hidetake.groovy.ssh.session.SessionHandler> {
                put(
                    hashMapOf(
                        "from" to File("${project.buildDir.absolutePath}/libs/cva-retrieval-pipeline-0.1-all.jar"),
                        "into" to "/home/esowc25/cva-retrieval-pipeline-app"
                    )
                )
                execute("sudo systemctl restart retrievalpipeline.service")
            })
        })
    }
}


