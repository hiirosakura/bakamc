import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.10"
    java
    signing
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
    maven { url = uri("https://maven.forpleuvoir.moe/releases") }
}

group = "cn.bakamc"
version = "0.1.0a"

tasks.apply {
    withType<JavaCompile>().configureEach {
        this.options.release
        this.options.encoding = "UTF-8"
        targetCompatibility = JavaVersion.VERSION_17.toString()
        sourceCompatibility = JavaVersion.VERSION_17.toString()
    }
    withType<KotlinCompile>().configureEach {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

sourceSets {
    getByName("test") {
        kotlin.srcDir("src/test/kotlin")
    }
}


java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

subprojects {

    apply {
        plugin("java")
        plugin("kotlin")
        plugin("signing")
        plugin("com.github.johnrengelman.shadow")
        plugin("maven-publish")
        plugin("org.jetbrains.kotlin.plugin.noarg")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://maven.forpleuvoir.moe/snapshots") }
        maven { url = uri("https://maven.forpleuvoir.moe/releases") }
    }

    group = rootProject.group
    version = rootProject.version

    dependencies {
        implementation(kotlin("reflect"))
        implementation(kotlin("stdlib"))
    }

    tasks.apply {
        withType<JavaCompile>().configureEach {
            this.options.release
            this.options.encoding = "UTF-8"
            targetCompatibility = JavaVersion.VERSION_17.toString()
            sourceCompatibility = JavaVersion.VERSION_17.toString()
        }
        withType<KotlinCompile>().configureEach {
            kotlinOptions.suppressWarnings = true
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    sourceSets {
        getByName("test") {
            kotlin.srcDir("src/test/kotlin")
        }
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

}