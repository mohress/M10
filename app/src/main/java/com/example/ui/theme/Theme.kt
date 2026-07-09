package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BentoColorScheme = lightColorScheme(
    primary = AccentTeal,
    secondary = AccentBlue,
    tertiary = WarningAmber,
    background = SlateBg,
    surface = SlateCard,
    surfaceVariant = DarkSurface,
    onPrimary = SlateCard,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BentoColorScheme,
        typography = Typography,
        content = content
    )
}
