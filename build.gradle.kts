import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("com.github.jmongard.git-semver-plugin")
    id("org.openjfx.javafxplugin")
}

repositories {
    mavenCentral()
}

javafx {
    modules = listOf("javafx.base")
}

dependencies {
    testImplementation("junit:junit:_")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:_")
}

semver {
    releaseTagNameFormat = "v%s"
}

version = semver.version

tasks {
    compileJava {
        options.compilerArgumentProviders.add(CommandLineArgumentProvider {
            // Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
            listOf("--patch-module", "de.treichels.hott.util=${sourceSets["main"].output.asPath}")
        })
    }

    jar {
        manifest {
            attributes (
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
            )
        }
    }

    withType(KotlinCompile::class) {
        kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.ExperimentalUnsignedTypes")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MDL-Viewer/hott-util")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("github") {
            artifactId = project.name.lowercase()
            group = "de.treichels.hott"
            from(components["java"])
        }          
    }
}
