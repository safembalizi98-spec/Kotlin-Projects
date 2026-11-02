
plugins {
    kotlin("jvm") version "2.2.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src")
        }
    }
}
