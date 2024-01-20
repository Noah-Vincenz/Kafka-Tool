import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    `java-library`

}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor", "ktor-client-gson", ktor_version)
    implementation("io.ktor", "ktor-client-features", ktor_version)
    implementation("org.apache.avro", "avro", "1.10.2")
    implementation("org.apache.kafka", "kafka-clients", "2.7.0")
    implementation("someotherserializer", "someotherserializer", "version")
    implementation("org.springframework.kafka", "spring-kafka", "2.6.5")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.4")
    implementation("io.ktor:ktor-server-core:$ktor_version")
}

repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
