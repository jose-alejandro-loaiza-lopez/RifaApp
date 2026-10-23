package com.rifaapp

import android.content.Context
import com.rifaapp.core.designsystem.theme.ThemeColor
import com.rifaapp.core.designsystem.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("rifa_prefs", Context.MODE_PRIVATE)
    private val _mode = MutableStateFlow(loadMode())
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    private val _color = MutableStateFlow(loadColor())
    val color: StateFlow<ThemeColor> = _color.asStateFlow()

    fun setMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _mode.value = mode
    }

    fun setColor(color: ThemeColor) {
        prefs.edit().putString("theme_color", color.name).apply()
        _color.value = color
    }

    private fun loadMode(): ThemeMode = try {
        ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.AUTO.name) ?: ThemeMode.AUTO.name)
    } catch (_: Exception) {
        ThemeMode.AUTO
    }

    private fun loadColor(): ThemeColor = try {
        ThemeColor.valueOf(prefs.getString("theme_color", ThemeColor.AUTO.name) ?: ThemeColor.AUTO.name)
    } catch (_: Exception) {
        ThemeColor.AUTO
    }
}