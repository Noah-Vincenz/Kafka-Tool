val kotlin_version: String by project
val spek_version: String by project
val jackson_version: String by project
val kluent_version: String by project
val kotest_version: String by project
val koin_version: String by project

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    }
}

plugins {
    java
}

allprojects {
    group = "com.noah"
    version = "0.0.1"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        mavenLocal()
	// maven {} ?
    }

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
        implementation("io.github.microutils", "kotlin-logging", "1.7.7")
        implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.0")
        implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-slf4j", "1.3.0")
        testImplementation("io.kotest", "kotest-runner-junit5-jvm", kotest_version)
        testImplementation("io.kotest", "kotest-assertions-core-jvm", kotest_version)
        testImplementation("io.kotest", "kotest-extensions-spring-jvm", kotest_version)
    }
//    tasks.withType<Test> {
//        useJUnitPlatform {
//            includeEngines("spek2")
//        }
//    }
}

subprojects {
    version = "1.0"
}

project(":kafkatool-shared") {

}

project(":kafkatool-service") {
    dependencies {
        implementation(project(":kafkatool-shared"))
    }
}

project(":dataaccess-service") {
    dependencies {
        implementation(project(":kafkatool-shared"))
    }
}

project(":kafkatool-restapi") {
    dependencies {
        implementation(project(":kafkatool-shared"))
        implementation(project(":dataaccess-service"))
    }
}

project(":kafkatool-web") {
    dependencies {
        implementation(project(":kafkatool-shared"))
        implementation(project(":kafkatool-restapi"))
        implementation(project(":kafkatool-service"))
    }
}
