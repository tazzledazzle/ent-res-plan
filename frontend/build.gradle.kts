// build.gradle.kts
plugins {
    kotlin("js") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

group = "com.erp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            binaries.executable()
        }
    }
}

dependencies {
    // React Wrappers
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.625")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.625")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.11.1-pre.625")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.11.2-pre.625")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // HTTP Client
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-js:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
}