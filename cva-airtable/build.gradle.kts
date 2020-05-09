import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val micronautVersion: String by project
val airtableVersion: String by project

plugins {
    groovy
    application
}

version = "0.1"

application {
    mainClassName = "com._2horizon.cva.airtable.Application"
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {

    implementation(project(":cva-common"))

    // https://github.com/fuxingloh/airtable
    implementation("dev.fuxing:airtable-api:$airtableVersion")

    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("io.micronaut:micronaut-runtime")

    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")



}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    withType<ShadowJar> {
        mergeServiceFiles()
    }
    withType<JavaExec> {
        classpath += configurations.getByName("developmentOnly")
        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }
}

allOpen{
    annotation("io.micronaut.aop.Around")
}

