plugins {
	id("java")
}

group = "red.aviora.redmc"
version = "1.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
	compileOnly("dev.folia:folia-api:1.21.11-R0.1-SNAPSHOT")
	implementation("fr.mrmicky:fastboard:2.1.5")
	implementation(project(":plugins:api"))
	implementation(project(":plugins:placeholders"))
	implementation(project(":plugins:vault"))
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.jar {
	dependsOn(configurations.runtimeClasspath)
	from(configurations.runtimeClasspath.get()
		.filter { it.name.contains("fastboard") }
		.map { zipTree(it) }
	)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
