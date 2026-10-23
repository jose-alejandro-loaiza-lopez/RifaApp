// Build file raíz: solo declara los plugins, no los aplica.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
}
