plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.paperUserDev)
    alias(libs.plugins.shadow)
}

val minecraftVersion = "1.21.11"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.foliaDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")

    implementation(project(":nebula-config"))
    implementation(project(":bakamc-common"))
}


paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

}

tasks {

    runServer {
        minecraftVersion(minecraftVersion)
    }

    assemble {
        dependsOn(reobfJar)
    }


    shadowJar {
        dependencies {
            include(dependency(":nebula-common"))
            include(dependency(":nebula-serialization"))
            include(dependency(":nebula-config"))
            include(dependency(":bakamc-common"))
        }

    }

}