dependencies {
    compileOnly(libs.adventureApi)
    api(libs.nebula.scala)
    compileOnly(libs.slf4j)

    testImplementation(libs.adventureApi)
}