package com.notas.personal.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.ExperimentalTextApi
import com.notas.personal.R

@OptIn(ExperimentalTextApi::class)
private val Nunito = FontFamily(
    Font(R.font.nunito, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.nunito, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.nunito, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.nunito, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6754C0), onPrimary = Color.White,
    primaryContainer = Color(0xFFECE5FA), onPrimaryContainer = Color(0xFF29213F),
    secondary = Color(0xFF78572D), secondaryContainer = Color(0xFFF6DEB8), onSecondaryContainer = Color(0xFF503714),
    background = Color(0xFFFBF8F3), onBackground = Color(0xFF292533),
    surface = Color(0xFFFBF8F3), onSurface = Color(0xFF292533),
    surfaceVariant = Color(0xFFF0ECE8), onSurfaceVariant = Color(0xFF68616F),
    outline = Color(0xFF817888), outlineVariant = Color(0xFFE6DFE8),
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFFC8BAFF), onPrimary = Color(0xFF32215F),
    primaryContainer = Color(0xFF38304F), onPrimaryContainer = Color(0xFFF0E8FF),
    secondary = Color(0xFFE9B86E), secondaryContainer = Color(0xFF4E3D27), onSecondaryContainer = Color(0xFFF7DDB5),
    background = Color(0xFF141622), onBackground = Color(0xFFF0EDF5),
    surface = Color(0xFF141622), onSurface = Color(0xFFF0EDF5),
    surfaceVariant = Color(0xFF222432), onSurfaceVariant = Color(0xFFBFB9CD),
    outline = Color(0xFF938B9F), outlineVariant = Color(0xFF343343),
)

@Composable
fun NotesTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val base = Typography()
    val rounded = Nunito
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography(
            displaySmall = base.displaySmall.copy(fontFamily = rounded),
            headlineLarge = base.headlineLarge.copy(fontFamily = rounded),
            headlineSmall = base.headlineSmall.copy(fontFamily = rounded),
            titleLarge = base.titleLarge.copy(fontFamily = rounded),
            titleMedium = base.titleMedium.copy(fontFamily = rounded),
            bodyLarge = base.bodyLarge.copy(fontFamily = rounded),
            bodyMedium = base.bodyMedium.copy(fontFamily = rounded),
            labelLarge = base.labelLarge.copy(fontFamily = rounded),
            labelMedium = base.labelMedium.copy(fontFamily = rounded),
            labelSmall = base.labelSmall.copy(fontFamily = rounded),
            bodySmall = base.bodySmall.copy(fontFamily = rounded),
        ),
        content = content,
    )
}
