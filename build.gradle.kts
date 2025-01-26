plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.tradecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

val embed: Configuration by configurations.creating
configurations.compileOnly.get().extendsFrom(embed)

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))

    embed("io.github.classgraph:classgraph:4.8.179")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Database
    embed("org.hibernate.orm:hibernate-core:6.6.5.Final")
    embed("org.hibernate.orm:hibernate-community-dialects:6.6.5.Final")

    // Database options
    embed("com.h2database:h2:2.3.232")
    embed("org.xerial:sqlite-jdbc:3.48.0.0")

    // Web
    embed("io.undertow:undertow-core:2.3.18.Final")
    embed("io.undertow:undertow-websockets-jsr:2.3.18.Final")
    embed("com.google.code.gson:gson:2.11.0")
    embed("org.springframework.security:spring-security-web:6.4.2")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    from(
        embed.resolve().map {
            if (it.isDirectory) it else zipTree(it).matching { exclude { it.name.endsWith("module-info.class") } }
        }) {}
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.runServer {
    minecraftVersion("1.21.4")
}