plugins {
	id("java")
	id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "red.aviora.redmc"
version = "0.0.1-alpha"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
	paperweight.foliaDevBundle("1.21.11-R0.1-SNAPSHOT")
	implementation(project(":plugins:api"))
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
