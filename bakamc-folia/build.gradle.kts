@file:Suppress("VulnerableLibrariesLocal")

repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

val nebulaVersion: String = "0.2.5d"

dependencies {
    api("moe.forpleuvoir:nebula:$nebulaVersion") {
        exclude("moe.forpleuvoir", "nebula-event")
    }
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    implementation("com.mysql:mysql-connector-j:8.1.0")
    implementation("com.baomidou:mybatis-plus:3.1.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

noArg{
    annotation("cn.bakamc.folia.util.NoArg")
    invokeInitializers = true
}