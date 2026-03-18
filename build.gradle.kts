plugins {
	id("xyz.jpenilla.run-paper") version "3.0.0"
}

val runFolia = runPaper.folia.registerTask {
	version("1.21.11")
	dependsOn(
		project(":plugins:api").tasks.named("jar"),
		project(":plugins:permissions").tasks.named("jar"),
		project(":plugins:placeholders").tasks.named("jar"),
		project(":plugins:vault").tasks.named("jar"),
		project(":plugins:tab").tasks.named("jar"),
		project(":plugins:scoreboard").tasks.named("jar"),
		project(":plugins:chat").tasks.named("jar"),
		project(":plugins:npc").tasks.named("reobfJar"),
		project(":plugins:motd").tasks.named("jar"),
		project(":plugins:teleport").tasks.named("jar"),
		project(":plugins:perks").tasks.named("reobfJar"),
		project(":plugins:cosmetics").tasks.named("jar"),
		project(":plugins:holograms").tasks.named("jar")
	)
	pluginJars(
		project(":plugins:api").tasks.named("jar"),
		project(":plugins:permissions").tasks.named("jar"),
		project(":plugins:placeholders").tasks.named("jar"),
		project(":plugins:vault").tasks.named("jar"),
		project(":plugins:tab").tasks.named("jar"),
		project(":plugins:scoreboard").tasks.named("jar"),
		project(":plugins:chat").tasks.named("jar"),
		project(":plugins:npc").tasks.named("reobfJar"),
		project(":plugins:motd").tasks.named("jar"),
		project(":plugins:teleport").tasks.named("jar"),
		project(":plugins:perks").tasks.named("reobfJar"),
		project(":plugins:cosmetics").tasks.named("jar"),
		project(":plugins:holograms").tasks.named("jar"),
	)
	doFirst {
		val pluginsDir = runDirectory.get().asFile.resolve("plugins")
		pluginsDir.deleteRecursively()
	}
}
