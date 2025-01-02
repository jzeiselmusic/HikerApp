package com.jz.hiker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = StravaOrange,
    secondary = BasicallyWhite40,
    tertiary = LightGrey40,
    background = DarkGrey40,
)

@Composable
fun HikerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}