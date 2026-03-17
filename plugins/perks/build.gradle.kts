plugins {
	id("java")
}

group = "red.aviora.redmc"
version = "1.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	compileOnly("dev.folia:folia-api:1.21.11-R0.1-SNAPSHOT")
	implementation(project(":plugins:api"))
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
