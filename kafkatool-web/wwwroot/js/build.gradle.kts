import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by project
val ktor_version: String by project
val kotest_version: String by project
val koin_version: String by project
val h2_version: String by project
val exposed_version: String by project

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

plugins {
    application
    id("com.github.johnrengelman.shadow") version "4.0.1"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

repositories {
    mavenLocal()
    jcenter()
    // maven { url = uri(="") }
}

dependencies {
    implementation(".avro", "schemas" ,"1.0.0")
    implementation("org.apache.avro", "avro", "1.10.2")
    implementation("io.confluent", "kafka-avro-serializer", "5.3.1")
    implementation("io.confluent", "kafka-schema-registry-client", "5.3.1")
    implementation("org.apache.kafka", "kafka-clients", "2.7.0")
    implementation("...", "kafka-secure-serializers", "..")
    implementation("io.ktor", "ktor-jackson", ktor_version)
    implementation("io.ktor", "ktor-client-apache", ktor_version)
    implementation("io.ktor", "ktor-client-json", ktor_version)
    implementation("io.ktor", "ktor-client-gson", ktor_version)
    implementation("io.ktor", "ktor-client-features", ktor_version)
    implementation("io.ktor", "ktor-server-cio", ktor_version)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.0.1")
    implementation("org.koin", "koin-core", koin_version)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("com.zaxxer:HikariCP:2.7.8")
    implementation("com.viartemev:ktor-flyway-feature:1.0.0")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set("") // to disable adding version at the end of JAR filename
        archiveBaseName.set("kafkatool-restapi")
        archiveClassifier.set(null as String?)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.noah.Application.kt"))
        }
    }
}
