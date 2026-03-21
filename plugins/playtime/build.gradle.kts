plugins {
	id("java")
}

group = "red.aviora.redmc"
version = "0.0.1-alpha"

dependencies {
	compileOnly("dev.folia:folia-api:1.21.11-R0.1-SNAPSHOT")
	implementation(project(":plugins:api"))
	implementation(project(":plugins:placeholders"))
	implementation(project(":plugins:vault"))
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
