//plugins {
////    id 'com.pswidersk.python-plugin' version '2.8.2'
//}

// build.gradle.kts
plugins {
    kotlin("multiplatform") version "1.9.20"
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
/*
* The latest versions of the specified Kotlin wrappers as of February 13, 2025, are:
	•	kotlin-react: `2025.2.0-19.0.0`.
	•	kotlin-emotion: `2025.1.11` (as part of the Kotlin Wrappers BOM).
	•	kotlin-react-router: `2025.1.11` (as part of the Kotlin Wrappers BOM).
* */
val kotlinReactVersion = "2025.2.0-19.0.0"
val kotlinEmotionVersion = "2025.1.11"
val kotlinReactDom = "2025.1.6-6.28.0"
val ktorVersion = "3.1.0"
val kotlinVersion = "1.9.20"
kotlin {
    js {
        sourceSets {
            val jsMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                    implementation("io.ktor:ktor-client-core:$ktorVersion")
                    implementation("io.ktor:ktor-client-js:$ktorVersion")
                    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$kotlinReactVersion")
                    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$kotlinReactVersion")
                    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:$kotlinEmotionVersion")
                    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$kotlinReactDom")
                }
            }
        }
    }
}
dependencies {
//    // React Wrappers
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$kotlinReactVersion")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$kotlinReactVersion")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:$kotlinEmotionVersion")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$kotlinReactDom")
//
//    // Coroutines
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
//
//    // Serialization
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
//
//    // HTTP Client
//    implementation("io.ktor:ktor-client-core:$ktorVersion")
//    implementation("io.ktor:ktor-client-js:$ktorVersion")
//    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

