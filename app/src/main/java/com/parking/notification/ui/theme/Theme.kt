package com.parking.notification.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLight,
    background = Background,
    surface = Surface,
    surfaceVariant = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Divider,
    error = Error,
    onError = OnPrimary
)

@Composable
fun ParkingNotificationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
