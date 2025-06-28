pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "bakamc"

include(":bakamc-folia")
include(":bakamc-common")
include(":bakamc-velocity")