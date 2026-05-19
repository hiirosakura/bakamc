plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.paperUserDev)
    alias(libs.plugins.shadow)
}

val minecraftVersion = "26.1.2"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/creatorfromhell/")
    maven("https://repo.extendedclip.com/releases/")
    maven {
        name = "menthamc"
        url = uri("https://repo.menthamc.org/repository/maven-public/")
    }
}

dependencies {
//    paperweight.foliaDevBundle("$minecraftVersion.build.8-stable")
    paperweight.devBundle("me.earthme.luminol", "26.1.2.build.648-stable")

    implementation(libs.nebula.scala)
    implementation(project(":bakamc-common"))

    compileOnly(libs.slick)
    compileOnly(libs.slickHikariCP)
    compileOnly(libs.hikariCP)
    compileOnly(libs.mysqlConnector)

    compileOnly(libs.vaultUnlockedApi) { isTransitive = false }
    compileOnly(libs.placeholderApi)

}


paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

}

tasks {

    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "projectVersion" to version,
//            "apiVersion" to minecraftVersion.substringBeforeLast('.'),
            "description" to "这是什么插件"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion(minecraftVersion)
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependencies {
            include(dependency(libs.nebula.scala))
            include(dependency(":bakamc-common"))
        }

    }

}