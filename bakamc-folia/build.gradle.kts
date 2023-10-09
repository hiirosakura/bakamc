repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    implementation(project(":bakamc-common"))
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
}

