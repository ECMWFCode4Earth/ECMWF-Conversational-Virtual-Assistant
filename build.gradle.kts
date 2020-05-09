

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
val micronautVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project
val jacksonKotlinVersion: String by project
val objenesisVersion: String by project
val spockVersion: String by project

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
    jacoco
}

subprojects {
    if (name.startsWith("cva")) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "name.remal.check-dependency-updates")
        apply(plugin = "org.jlleitschuh.gradle.ktlint-idea")
    }

    // All Micronaut subproject
    if (listOf("cva-airtable", "cva-retrieval-pipeline").contains(name)) {
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
        apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
        apply(plugin = "com.github.johnrengelman.shadow")
        apply(plugin = "com.google.cloud.tools.jib")

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
