rootProject.name = "redmc"

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.papermc.io/repository/maven-public/")
	}
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://repo.papermc.io/repository/maven-public/")
		maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
		maven("https://repo.dmulloy2.net/repository/public/")
	}
}

include(
	"plugins:api",
	"plugins:permissions",
	"plugins:placeholders",
	"plugins:vault",
	"plugins:tab",
	"plugins:scoreboard",
	"plugins:chat",
	"plugins:npc"
)