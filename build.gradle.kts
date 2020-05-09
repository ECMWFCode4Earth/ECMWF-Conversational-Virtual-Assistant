allprojects {
    group = "com._2horizon.cva"
}

subprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://jcenter.bintray.com")
    }
}

val kotlinVersion: String by project

plugins {
    java
    kotlin("jvm") apply false
    kotlin("kapt") apply false
    kotlin("plugin.jpa") apply false
    kotlin("plugin.allopen") apply false
    kotlin("plugin.noarg") apply false
    id("name.remal.check-dependency-updates") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("org.jlleitschuh.gradle.ktlint-idea") apply false
    id("com.google.cloud.tools.jib") apply false
    jacoco
}

subprojects {
    if (name.startsWith("cva")) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "name.remal.check-dependency-updates")
        apply(plugin = "org.jlleitschuh.gradle.ktlint-idea")
    }
    if (name.startsWith("cva-r")) {
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
        apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
        apply(plugin = "com.github.johnrengelman.shadow")
        apply(plugin = "com.google.cloud.tools.jib")
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
