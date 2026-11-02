plugins {
    kotlin("jvm") version "2.3.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.xerial:sqlite-jdbc:3.54.1.0")

}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}