import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by project
val ktor_version: String by project
val kotest_version: String by project
val koin_version: String by project

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
    maven { url = uri("..someurl") }
}

dependencies {
    // ...avro schema urls
    implementation("org.apache.avro", "avro", "1.10.1")
    implementation("io.confluent", "kafka-avro-serializer", "5.3.1")
    implementation("org.apache.kafka", "kafka-clients", "2.7.0")
    implementation("someserializer", "someserializer", "someversion")
    implementation("org.springframework.kafka", "spring-kafka", "2.6.5")
    implementation("io.ktor", "ktor-server-netty", ktor_version)
    implementation("org.koin", "koin-core", koin_version)
    testImplementation("io.ktor", "ktor-server-tests", ktor_version)
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-mustache:$ktor_version")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.0.1")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveVersion.set("") // to disable adding version at the end of JAR filename
        archiveBaseName.set("kafkatool-webapp")
        archiveClassifier.set(null as String?)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.noah.Application.kt"))
        }
    }
}
