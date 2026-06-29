plugins {
	id("org.jetbrains.kotlin.multiplatform")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	jvm("android")
	iosX64()
	iosArm64()
	iosSimulatorArm64()
	jvmToolchain(11)

	targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).configureEach {
		binaries.framework {
			baseName = "Shared"
			isStatic = true
		}
	}

	sourceSets {
		commonMain.dependencies {
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.serialization.kotlinx.json)
			implementation(libs.kotlinx.serialization.json)
		}

		val androidMain by getting {
			dependencies {
				implementation(libs.ktor.client.okhttp)
			}
		}

		val iosMain by creating {
			dependsOn(commonMain.get())
			dependencies {
				implementation(libs.ktor.client.darwin)
			}
		}

		iosX64Main.get().dependsOn(iosMain)
		iosArm64Main.get().dependsOn(iosMain)
		iosSimulatorArm64Main.get().dependsOn(iosMain)
	}
}
