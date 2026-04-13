pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")

    }
}

rootProject.name = "bakamc"
include("nebula-common")
include("nebula-serialization")
include("nebula-config")


include("bakamc-common")
include("bakamc-folia")
