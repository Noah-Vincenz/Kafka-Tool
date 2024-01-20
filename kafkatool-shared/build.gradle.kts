val arrow_version: String by project
val ktor_version: String by project
val klaxon_version: String by project

dependencies {
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-json:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-slf4j", "1.3.0")
}

