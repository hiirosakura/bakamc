import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.10"
    java
    signing
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.2.0" // Adds runServer and runMojangMappedServer tasks for testing
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


dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    paperweight.foliaDevBundle("1.20.1-R0.1-SNAPSHOT")

    api("moe.forpleuvoir:nebula:0.2.6c") {
        exclude("moe.forpleuvoir", "nebula-event")
    }
    //data base
    apply {
        val ktormVersion = "3.6.0"
        runtimeOnly("com.mysql:mysql-connector-j:8.1.0")
        implementation("org.ktorm:ktorm-core:${ktormVersion}")
        implementation("org.ktorm:ktorm-support-mysql:${ktormVersion}")
        implementation("com.zaxxer:HikariCP:5.0.1")
    }
}

group = "cn.bakamc"
version = "0.1.0a"
description = "这是什么插件"

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

tasks {

    assemble {
        dependsOn(reobfJar)
    }

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
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

sourceSets {
    getByName("test") {
        kotlin.srcDir("src/test/kotlin")
    }
}


noArg {
    annotation("cn.bakamc.folia.util.NoArg")
    invokeInitializers = true
}