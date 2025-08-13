package buildsrc.convention

import buildsrc.config.Deps
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.library")

    kotlin("android")

    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")

    id("buildsrc.convention.base")
}

android {
    compileSdk = Deps.Versions.compileSdk

    lint {
        abortOnError = false
        disable += "InvalidPackage"
        warning += "NewApi"
    }

    packaging {
        resources {
            excludes += "META-INF/main.kotlin_module"
        }
    }

    defaultConfig {
        minSdk = Deps.Versions.minSdk
        targetSdk = Deps.Versions.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    compileOptions {
        sourceCompatibility = Deps.Versions.jvmTarget
        targetCompatibility = Deps.Versions.jvmTarget
    }

    publishing {
        singleVariant("release") {}
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(Deps.Versions.jvmTarget.toString()))
    }
}

dependencies {
    testImplementation("junit:junit:${Deps.Versions.junit4}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Deps.Versions.androidxEspresso}")

    androidTestImplementation("androidx.test:rules:${Deps.Versions.androidxTestRules}")
    androidTestImplementation("androidx.test:runner:${Deps.Versions.androidxTestRunner}")
    androidTestImplementation("androidx.test.ext:junit-ktx:${Deps.Versions.androidxTestExtJunit}")
    androidTestUtil("androidx.test:orchestrator:${Deps.Versions.androidxTestOrchestrator}")

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test-junit"))
    androidTestUtil("androidx.test:orchestrator:${Deps.Versions.androidxOrchestrator}")
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
}
