rootProject.name = "ECMWF-virtual-assistant"

include(
    "cva-common",
    "cva-airtable",
    "cva-retrieval-pipeline",
    "cva-df-fulfillment",
    "cva-df-manager",
    "cva-copernicus",
    "cva-nlp"
)

pluginManagement {
    plugins {
        fun String.getVersion() = extra["$this.version"].toString()
        fun PluginDependenciesSpec.resolve(id: String, versionKey: String = id) = id(id) version versionKey.getVersion()

        resolve("org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.kapt", "org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.plugin.jpa", "org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.plugin.allopen", "org.jetbrains.kotlin.jvm")
        resolve("org.jetbrains.kotlin.plugin.noarg", "org.jetbrains.kotlin.jvm")
        resolve("com.github.johnrengelman.shadow")
        resolve("name.remal.check-dependency-updates")
        resolve("com.google.cloud.tools.jib")
        resolve("org.jlleitschuh.gradle.ktlint-idea")
        resolve("org.hidetake.ssh")
        // resolve("org.asciidoctor.jvm.convert")
        // resolve("org.asciidoctor.jvm.pdf")
    }
}
