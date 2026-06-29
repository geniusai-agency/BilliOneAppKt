import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	id("org.jetbrains.kotlin.multiplatform")
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.jetbrains.compose)
}

kotlin {
	jvm("desktop")
	jvmToolchain(17)

	sourceSets {
		val desktopMain by getting {
			dependencies {
				implementation(compose.desktop.currentOs)
				implementation(compose.material3)
				implementation(compose.materialIconsExtended)
				implementation(libs.ktor.client.cio)
				implementation(libs.ktor.client.content.negotiation)
				implementation(libs.ktor.serialization.kotlinx.json)
				implementation(libs.kotlinx.serialization.json)
			}
		}
	}
}

compose.desktop {
	application {
		mainClass = "com.example.billionemotosappkt.desktop.MainKt"
		nativeDistributions {
			packageName = "BilliOneMotosAppKt"
			packageVersion = "1.0.0"
			targetFormats(TargetFormat.Deb)
			windows {
				iconFile.set(project.file("src/desktopMain/resources/logo_billione.ico"))
			}
			linux {
				iconFile.set(project.file("src/desktopMain/resources/logo_billione.png"))
			}
			includeAllModules = true
		}
	}
}
