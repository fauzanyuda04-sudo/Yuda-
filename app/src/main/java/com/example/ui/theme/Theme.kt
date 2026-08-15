package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JoyfulKidsColorScheme = lightColorScheme(
    primary = AlnauraSkyBlue,
    onPrimary = Color.White,
    secondary = AlnauraCoral,
    onSecondary = Color.White,
    tertiary = AlnauraYellowDark,
    onTertiary = TextDarkNavy,
    background = PastelCreamBg,
    onBackground = TextDarkNavy,
    surface = CardBackgroundWhite,
    onSurface = TextDarkNavy,
    surfaceVariant = SurfaceSoftYellow,
    onSurfaceVariant = TextSubtitle,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = JoyfulKidsColorScheme,
        typography = Typography,
        content = content
    )
}

