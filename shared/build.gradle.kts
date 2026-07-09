plugins {
	id("org.jetbrains.kotlin.multiplatform")
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
	android {
		namespace = "com.example.billionemotosappkt.shared"
		compileSdk = 37
		minSdk = 24
		compilerOptions {
			jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
		}
	}
	jvm()
	iosArm64()
	iosSimulatorArm64()
	jvmToolchain(17)

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
			
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.materialIconsExtended)
		}

		val androidMain by getting {
			dependencies {
				implementation(libs.ktor.client.okhttp)
				implementation(libs.androidx.activity.compose)
				implementation(libs.core.ktx)
				implementation(libs.androidx.lifecycle.viewmodel.ktx)
				implementation(libs.androidx.lifecycle.viewmodel.compose)
			}
		}

		val jvmMain by getting {
			dependencies {
				implementation(libs.ktor.client.cio)
				implementation("org.apache.pdfbox:pdfbox:3.0.7")
			}
		}

		val iosMain by creating {
			dependsOn(commonMain.get())
			dependencies {
				implementation(libs.ktor.client.darwin)
			}
		}

		iosArm64Main.get().dependsOn(iosMain)
		iosSimulatorArm64Main.get().dependsOn(iosMain)
	}
}
