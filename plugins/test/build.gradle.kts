plugins {
	id("java")
	id("xyz.jpenilla.run-paper") version "3.0.0"
}

group = "red.aviora.redmc"
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

val runFolia = runPaper.folia.registerTask {
	version("1.21.8")
	dependsOn(
		project(":plugins:api").tasks.named("reobfJar"),
		project(":plugins:permissions").tasks.named("jar")
	)
	pluginJars(
		project(":plugins:api").tasks.named("reobfJar"),
		project(":plugins:permissions").tasks.named("jar")
	)
	downloadPlugins {
		github("dmulloy2", "ProtocolLib", "5.4.0", "ProtocolLib.jar")
	}
}