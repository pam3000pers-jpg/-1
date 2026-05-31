package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryCrimson,
    secondary = SecondaryGold,
    tertiary = TrollBlue,
    background = VelvetBlack,
    surface = VelvetCard,
    onPrimary = Color.White,
    onSecondary = Color(0xFF120202),
    onBackground = Color(0xFFFFF1F1),
    onSurface = Color(0xFFFFF1F1)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryCrimson,
    secondary = SecondaryGold,
    tertiary = TrollBlue,
    background = Color(0xFFFFF5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color(0xFF120202),
    onBackground = Color(0xFF120202),
    onSurface = Color(0xFF260D0D)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Force our customized theater theme for the full immersive experience
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
