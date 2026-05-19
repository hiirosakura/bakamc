pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")

    }
}

rootProject.name = "bakamc"
include("bakamc-common")
include("bakamc-folia")
