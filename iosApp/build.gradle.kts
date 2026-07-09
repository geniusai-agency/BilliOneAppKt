plugins {
	id("org.jetbrains.kotlin.multiplatform")
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.jetbrains.compose)
}

kotlin {
	iosArm64()
	iosSimulatorArm64()
	jvmToolchain(17)

	targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
		binaries.framework {
			baseName = "BilliOneMotosIosApp"
			isStatic = true
		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(":shared"))
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.materialIconsExtended)
			}
		}
	}
}
