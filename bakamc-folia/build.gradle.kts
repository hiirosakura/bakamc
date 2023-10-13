repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

val nebulaVersion: String = "0.2.5c"
val mbpVersion: String = "3.5.3.2"

dependencies {
    api("moe.forpleuvoir:nebula:$nebulaVersion") {
        exclude("moe.forpleuvoir", "nebula-event")
    }
    implementation("com.baomidou:mybatis-plus:$mbpVersion")
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
}

