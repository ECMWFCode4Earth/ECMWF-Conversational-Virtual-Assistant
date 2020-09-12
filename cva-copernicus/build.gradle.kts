import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val micronautVersion: String by project
val micronautGraphQLVersion: String by project
val graphqlJavaFederationVersion: String by project
val graphqlJavaExtendedScalarsVersion: String by project
val okHttpVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project
val checkDependencyUpdatesVersion: String by project
val jacksonKotlinVersion: String by project
val objenesisVersion: String by project
val spockVersion: String by project
val jsoupVersion: String by project
val micronautReactorVersion: String by project
val solrjVersion: String by project
val solrjAsyncVersion: String by project


plugins {
    groovy
    application
}

version = "0.1"

application {
    mainClassName = "com._2horizon.cva.copernicus.Application"
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {

    //https://micronaut-projects.github.io/micronaut-reactor/latest/guide/
    implementation("io.micronaut.reactor:micronaut-reactor:$micronautReactorVersion")

    // https://lucene.apache.org/solr/guide
    implementation("org.apache.solr:solr-solrj:$solrjVersion")

    // https://inoio.github.io/solrs/
    implementation("io.ino:solrs_2.12:$solrjAsyncVersion")

    // https://jsoup.org/
    implementation("org.jsoup:jsoup:$jsoupVersion")

    implementation(project(":cva-common"))

    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-management")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-runtime")

    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kapt("io.micronaut.configuration:micronaut-openapi")

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

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
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


