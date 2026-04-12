dependencies {
    compileOnly(libs.adventureApi)
    api(project(":nebula-config"))
    compileOnly(libs.slf4j)

    testImplementation(libs.adventureApi)
}