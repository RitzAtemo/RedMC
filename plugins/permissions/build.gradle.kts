plugins {
    id("java")
}

group = "com.aviora.redmc"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("dev.folia:folia-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":plugins:api"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}