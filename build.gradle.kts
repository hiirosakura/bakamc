plugins {
    scala
    `java-library`
}

dependencies {
    // 所有模块都需要的 Scala 3.8 核心库
    implementation(rootProject.libs.scalaLibrary3)
}

allprojects {
    group = "moe.forpleuvoir"
    version = "1.0-SNAPSHOT"

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
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        // 所有模块都需要的 Scala 3.8 核心库
        implementation(rootProject.libs.scalaLibrary3)

        // 所有模块统一的测试配置
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



