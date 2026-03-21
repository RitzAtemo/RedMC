plugins {
	id("java")
	id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "red.aviora.redmc"
version = "1.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	paperweight.foliaDevBundle("1.21.11-R0.1-SNAPSHOT")
	implementation(project(":plugins:api"))
	implementation(project(":plugins:vault"))
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
	val reobfJar by named("reobfJar")
	assemble {
		dependsOn(reobfJar)
	}
}

paperweight {
	reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
