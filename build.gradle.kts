import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.withType
import org.gradle.api.tasks.bundling.Jar

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

var moduleName = "Project Codename Ferret"
var moduleClassifier = "beta-debug"
var moduleVersion = "1.0"

val lwjglVersion = "3.3.3"
val jomlVersion = "1.10.5"
val `joml-primitivesVersion` = "1.10.0"
val lwjglNatives = "natives-windows"
val imguiVersion = "1.86.11"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io")}
    maven { url = uri("https://repo.eclipse.org/content/groups/efxclipse/")}
}

javafx {
    version = "21.0.3"
    modules = listOf("javafx.controls", "javafx.media")
}

dependencies {
	implementation("org.eclipse.fx:org.eclipse.fx.drift:1.0.0.rc6")

	implementation("org.tinylog:tinylog-api:2.7.0")
	implementation("org.tinylog:tinylog-impl:2.7.0")

	// LuaJ
	implementation("org.luaj:luaj-jse:3.0.1")

	// ImGUI Implementation
	implementation("io.github.spair:imgui-java-binding:$imguiVersion")
    implementation("io.github.spair:imgui-java-lwjgl3:$imguiVersion")
    
    implementation("io.github.spair:imgui-java-natives-windows:$imguiVersion")

	// LWJGL Implementation
	implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

	implementation("org.lwjgl", "lwjgl")
	implementation("org.lwjgl", "lwjgl-assimp")
	implementation("org.lwjgl", "lwjgl-glfw")
	implementation("org.lwjgl", "lwjgl-openal")
	implementation("org.lwjgl", "lwjgl-opengl")
	implementation("org.lwjgl", "lwjgl-stb")
	runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
	implementation("org.joml", "joml", jomlVersion)
	implementation("org.joml", "joml-primitives", `joml-primitivesVersion`)
}


// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


tasks {
	// UberJar Setup
    val uberJar by creating(ShadowJar::class) {
		
		archiveBaseName.set("$moduleName") // 
        archiveVersion.set("$moduleVersion")
        archiveClassifier.set("$moduleClassifier")

        from(sourceSets.main.get().output)

		from(project.configurations.compileClasspath)

		mergeServiceFiles()

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        
       	manifest {
			   attributes["Manifest-Version"] = "1.0"
		}
        
    }
	
    assemble {
        dependsOn(uberJar)
    }
}