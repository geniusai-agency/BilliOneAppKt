package com.example.billionemotosappkt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
	background = DarkBackground,
	onBackground = DarkForeground,
	surface = DarkCard,
	onSurface = DarkCardForeground,
	surfaceVariant = DarkMuted,
	onSurfaceVariant = DarkMutedForeground,
	primary = GreenPrimary,
	onPrimary = DarkAccentForeground,
	primaryContainer = DarkAccent,
	onPrimaryContainer = DarkAccentForeground,
	secondary = DarkSecondary,
	onSecondary = DarkSecondaryForeground,
	secondaryContainer = DarkSecondary,
	onSecondaryContainer = DarkSecondaryForeground,
	tertiary = GreenAccent,
	onTertiary = DarkAccentForeground,
	tertiaryContainer = DarkAccent,
	onTertiaryContainer = DarkAccentForeground,
	error = DestructiveRed,
	errorContainer = DestructiveRed,
	onError = DarkForeground,
	onErrorContainer = DarkForeground,
	outline = DarkBorder,
	outlineVariant = DarkInput,
	scrim = DarkBackground,
	inverseSurface = DarkForeground,
	inverseOnSurface = DarkBackground,
	inversePrimary = GreenAccent
)

private val LightColorScheme = lightColorScheme(
	background = LightBackground,
	onBackground = LightForeground,
	surface = LightCard,
	onSurface = LightCardForeground,
	surfaceVariant = LightMuted,
	onSurfaceVariant = LightMutedForeground,
	primary = LightAccent,
	onPrimary = LightAccentForeground,
	primaryContainer = LightSecondary,
	onPrimaryContainer = LightForeground,
	secondary = LightSecondary,
	onSecondary = LightSecondaryForeground,
	secondaryContainer = LightSecondary,
	onSecondaryContainer = LightForeground,
	tertiary = GreenPrimary,
	onTertiary = LightAccentForeground,
	tertiaryContainer = LightSecondary,
	onTertiaryContainer = LightForeground,
	error = DestructiveRed,
	errorContainer = DestructiveRed,
	onError = LightForeground,
	onErrorContainer = LightForeground,
	outline = LightBorder,
	outlineVariant = LightInput,
	scrim = LightBackground,
	inverseSurface = LightForeground,
	inverseOnSurface = LightBackground,
	inversePrimary = GreenAccent
)

@Composable
fun BilliOneMotosAppKtTheme(
	darkTheme: Boolean = true,
	// Keep the brand colors stable instead of replacing them with dynamic system colors.
	dynamicColor: Boolean = false,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}
	
	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}
