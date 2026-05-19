plugins {
    scala
    `java-library`
}

dependencies {
    implementation(rootProject.libs.scalaLibrary3)
}

allprojects {
    group = "moe.forpleuvoir"
    version = "0.4.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }

}

subprojects {
    apply(plugin = "scala")
    apply(plugin = "java-library")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencies {
        implementation(rootProject.libs.scalaLibrary3)

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

        testImplementation("org.scalatest:scalatest_3:3.2.19")
        testRuntimeOnly("com.vladsch.flexmark:flexmark-all:0.64.8")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
    }

    tasks {
        withType<ScalaCompile> {
            scalaCompileOptions.apply {
                additionalParameters.addAll(
                    listOf(
                        "-java-output-version:21",
                        "-old-syntax",
                        "-Yexplicit-nulls"
                    )
                )
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

        test {
            testLogging {
                showStandardStreams = true
            }
        }
    }

}



