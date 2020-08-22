

allprojects {
    group = "com._2horizon.cva"
    repositories {
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
        maven("https://jcenter.bintray.com")
    }
}

subprojects {

}

val kotlinVersion: String by project
val micronautVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project
val jacksonKotlinVersion: String by project
val objenesisVersion: String by project
val byteBuddyVersion: String by project
val spockVersion: String by project
val asciidoctorjVersion: String by project

plugins {
    java
    kotlin("jvm") 
    kotlin("kapt")
    kotlin("plugin.jpa") apply false
    kotlin("plugin.allopen") apply false
    kotlin("plugin.noarg") apply false
    id("name.remal.check-dependency-updates") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("org.jlleitschuh.gradle.ktlint-idea") apply false
    id("com.google.cloud.tools.jib") apply false
    id("org.hidetake.ssh") apply false
    // id("org.asciidoctor.jvm.convert")
    // id("org.asciidoctor.jvm.pdf")
    jacoco
}

dependencies{
    // implementation("org.asciidoctor:asciidoctorj:$asciidoctorjVersion")
}

// tasks {
//     "asciidoctor"(org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
//
//         setSourceDir(file("asciidoc"))
//
//         setOutputDir(file("docs"))
//
//         sources(delegateClosureOf<PatternSet> {
//             include("ECMWF-Conversational-Virtual-Assistant.adoc")
//         })
//
//         options(
//             mapOf(
//                 "doctype" to "article",
//                 "ruby" to "erubis"
//             )
//         )
//
//         attributes(
//             mapOf(
//                 "source-highlighter" to "coderay",
//                 "toc" to "left",
//                 "idprefix" to "",
//                 "idseparator" to "-"
//             )
//         )
//     }
//     "asciidoctorPdf"(org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask::class) {
//
//         setSourceDir(file("asciidoc"))
//
//         sources(delegateClosureOf<PatternSet> {
//             include("ECMWF-Conversational-Virtual-Assistant.adoc")
//         })
//
//         options(
//             mapOf(
//                 "doctype" to "article",
//                 "ruby" to "erubis"
//             )
//         )
//
//         val path = project.projectDir.path
//
//         attributes(
//             mapOf(
//                 "imagesdir" to "$path/asciidoc/img",
//                 "definitiondir" to "$path/asciidoc/definition",
//                 "glossarydir" to "$path/asciidoc/glossary",
//                 "sectiondir" to "$path/asciidoc/section",
//
//                 "source-highlighter" to "coderay",
//                 "toc" to "left",
//                 "idprefix" to "",
//                 "idseparator" to "-",
//                 "toclevels" to "4",
//                 "icons" to "font",
//                 "experimental" to ""
//             )
//         )
//
//     }
//
//
// }

subprojects {
    if (name.startsWith("cva")) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "name.remal.check-dependency-updates")
        apply(plugin = "org.jlleitschuh.gradle.ktlint-idea")
    }

    // All Micronaut subproject
    if (listOf("cva-airtable", "cva-retrieval-pipeline", "cva-df-fulfillment", "cva-df-manager","cva-copernicus").contains(name)) {
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
        apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
        apply(plugin = "com.github.johnrengelman.shadow")
        apply(plugin = "com.google.cloud.tools.jib")
        apply(plugin = "org.hidetake.ssh")

        dependencies {
            implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
            implementation("io.micronaut:micronaut-http-client")
            implementation("io.micronaut:micronaut-management")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            implementation("io.micronaut:micronaut-http-server-netty")
            implementation("io.micronaut:micronaut-runtime")

            kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
            kapt("io.micronaut:micronaut-inject-java")
            kapt("io.micronaut:micronaut-validation")

            runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
            runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

            kaptTest(platform("io.micronaut:micronaut-bom:$micronautVersion"))
            kaptTest("io.micronaut:micronaut-inject-java")
            testImplementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
            testImplementation("io.micronaut.test:micronaut-test-junit5")
            testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
            testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
            testImplementation("org.spockframework:spock-core:$spockVersion") {
                exclude("org.codehaus.groovy", "groovy-all")
            }
            testImplementation("io.micronaut:micronaut-inject-groovy")
            testImplementation("io.micronaut.test:micronaut-test-spock")
            testImplementation("org.objenesis:objenesis:$objenesisVersion")
            testImplementation("net.bytebuddy:byte-buddy:$byteBuddyVersion")
        }
    }
}

tasks {
    val subProjectsClasses = subprojects.map { it.buildDir.resolve("classes/kotlin/main") }
    val subProjectsExecutionData = subprojects.map { it.buildDir.resolve("jacoco/test.exec") }

    register<JacocoReport>("jacocoRootReport") {
        classDirectories.setFrom(subProjectsClasses)
        executionData(subProjectsExecutionData)
        reports {
            html.isEnabled = false
            xml.isEnabled = true
            xml.destination = File("$buildDir/jacoco/report.xml")
        }
    }

    register<JacocoCoverageVerification>("jacocoCoverageVerification") {
        classDirectories.setFrom(subProjectsClasses)
        executionData(subProjectsExecutionData)
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal.valueOf(0.65)
                }
            }
        }
    }
}
