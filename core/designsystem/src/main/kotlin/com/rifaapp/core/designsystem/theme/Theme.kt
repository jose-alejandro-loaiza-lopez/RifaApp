package com.rifaapp.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PurpleLight = lightColorScheme()
private val PurpleDark = darkColorScheme()

// Paletas predefinidas (Material You sin color dinámico)
private val BlueLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),
)
private val BlueDark = darkColorScheme(
    primary = Color(0xFFA0CAFD),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBFC6DC),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD6BEE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
)

private val GreenLight = lightColorScheme(
    primary = Color(0xFF006C4C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF89F8C7),
    onPrimaryContainer = Color(0xFF002115),
    secondary = Color(0xFF4D6357),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9DB),
    onSecondaryContainer = Color(0xFF092016),
    tertiary = Color(0xFF3F6373),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC2E8FB),
    onTertiaryContainer = Color(0xFF001F29),
)
private val GreenDark = darkColorScheme(
    primary = Color(0xFF89D8B6),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF005139),
    onPrimaryContainer = Color(0xFF89F8C7),
    secondary = Color(0xFFB1CCC0),
    onSecondary = Color(0xFF1F352B),
    secondaryContainer = Color(0xFF354B41),
    onSecondaryContainer = Color(0xFFCFE9DB),
    tertiary = Color(0xFFA5CDDD),
    onTertiary = Color(0xFF083543),
    tertiaryContainer = Color(0xFF264B5A),
    onTertiaryContainer = Color(0xFFC2E8FB),
)

private val RedLight = lightColorScheme(
    primary = Color(0xFFB3261E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF9DEDC),
    onPrimaryContainer = Color(0xFF410E0B),
    secondary = Color(0xFF775652),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF2C1512),
    tertiary = Color(0xFF6750A4),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF21005D),
)
private val RedDark = darkColorScheme(
    primary = Color(0xFFF2B8B5),
    onPrimary = Color(0xFF601410),
    primaryContainer = Color(0xFF8C1D18),
    onPrimaryContainer = Color(0xFFF9DEDC),
    secondary = Color(0xFFFFB4AB),
    onSecondary = Color(0xFF442926),
    secondaryContainer = Color(0xFF5D3F3B),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFD0BCFF),
    onTertiary = Color(0xFF381E72),
    tertiaryContainer = Color(0xFF4F378B),
    onTertiaryContainer = Color(0xFFEADDFF),
)

private val OrangeLight = lightColorScheme(
    primary = Color(0xFF8B5000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC0),
    onPrimaryContainer = Color(0xFF2D1600),
    secondary = Color(0xFF725943),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFEDFC6),
    onSecondaryContainer = Color(0xFF291806),
    tertiary = Color(0xFF5D6237),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE3E7B5),
    onTertiaryContainer = Color(0xFF1B1D00),
)
private val OrangeDark = darkColorScheme(
    primary = Color(0xFFFFB870),
    onPrimary = Color(0xFF4A2800),
    primaryContainer = Color(0xFF6A3C00),
    onPrimaryContainer = Color(0xFFFFDCC0),
    secondary = Color(0xFFE0C2A6),
    onSecondary = Color(0xFF402C18),
    secondaryContainer = Color(0xFF59422D),
    onSecondaryContainer = Color(0xFFFEDFC6),
    tertiary = Color(0xFFC4CA9C),
    onTertiary = Color(0xFF2F320B),
    tertiaryContainer = Color(0xFF454921),
    onTertiaryContainer = Color(0xFFE3E7B5),
)

private val TealLight = lightColorScheme(
    primary = Color(0xFF006A6A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF6FF7F7),
    onPrimaryContainer = Color(0xFF002020),
    secondary = Color(0xFF4A6363),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E8),
    onSecondaryContainer = Color(0xFF051F1F),
    tertiary = Color(0xFF4C6078),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4FF),
    onTertiaryContainer = Color(0xFF061D31),
)
private val TealDark = darkColorScheme(
    primary = Color(0xFF80D4D4),
    onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF005050),
    onPrimaryContainer = Color(0xFF6FF7F7),
    secondary = Color(0xFFB1CCCC),
    onSecondary = Color(0xFF1B3535),
    secondaryContainer = Color(0xFF324B4B),
    onSecondaryContainer = Color(0xFFCCE8E8),
    tertiary = Color(0xFFB3C9E5),
    onTertiary = Color(0xFF1E3246),
    tertiaryContainer = Color(0xFF35485F),
    onTertiaryContainer = Color(0xFFD3E4FF),
)

@Composable
fun RifaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeColor: ThemeColor = ThemeColor.AUTO,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        themeColor == ThemeColor.AUTO && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        themeColor == ThemeColor.AUTO -> if (darkTheme) PurpleDark else PurpleLight

        else -> when (themeColor) {
            ThemeColor.PURPLE -> if (darkTheme) PurpleDark else PurpleLight
            ThemeColor.BLUE -> if (darkTheme) BlueDark else BlueLight
            ThemeColor.GREEN -> if (darkTheme) GreenDark else GreenLight
            ThemeColor.RED -> if (darkTheme) RedDark else RedLight
            ThemeColor.ORANGE -> if (darkTheme) OrangeDark else OrangeLight
            ThemeColor.TEAL -> if (darkTheme) TealDark else TealLight
            else -> if (darkTheme) PurpleDark else PurpleLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}