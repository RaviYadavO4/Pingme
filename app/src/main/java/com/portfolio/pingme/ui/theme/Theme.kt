package com.portfolio.pingme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PurpleColorScheme = lightColorScheme(
    primary = PurpleMain,
    secondary = PurpleBubbleMe,
    background = PurpleLightBg,
    surface = WhiteCard,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextMuted
)

@Composable
fun PingMeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PurpleColorScheme,
        content = content
    )
}
