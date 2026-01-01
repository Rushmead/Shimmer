plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven (  "https://maven.neoforged.net/releases/" )
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)
}

val common by configurations.creating
val shadowCommon by configurations.creating
val developmentNeoForge = configurations.named("developmentNeoForge")

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    developmentNeoForge.get().extendsFrom(common)
}

dependencies {
    neoForge("net.neoforged:neoforge:$neoforge_version")

    common(project(path = ":Common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":Common", configuration = "transformProductionNeoForge")) { isTransitive = false }

    // There is no flywheel 1.20.4 version currently
    //modImplementation("com.jozufozu.flywheel:flywheel-forge-$minecraft_version:$forge_flywheel_version")

    forgeRuntimeLibrary("icyllis.modernui:ModernUI-Core:$modernui_core_version")
    modCompileOnly("icyllis.modernui:ModernUI-NeoForge:${minecraft_version}-${modernui_version}")

    modCompileOnly("maven.modrinth:sodium:mc1.21.1-0.6.13-neoforge")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
    // There is no oculus 1.20.4 version currently
    modCompileOnly("maven.modrinth:iris:1.8.12+1.21.1-neoforge")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")

    configurations = listOf(shadowCommon)

    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    val shadowJarTask = tasks.shadowJar.get()
    inputFile.set(shadowJarTask.archiveFile)
    dependsOn(shadowJarTask)
    archiveClassifier.set(null as String?)
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.sourcesJar {
    val commonSources = project(":Common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.get().archiveFile.map(project::zipTree))
}

components.getByName<SoftwareComponent>("java") {
    (this as AdhocComponentWithVariants).apply {
        withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
            skip()
        }
    }
}