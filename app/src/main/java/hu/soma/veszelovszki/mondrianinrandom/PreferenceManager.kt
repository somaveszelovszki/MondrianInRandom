package hu.soma.veszelovszki.mondrianinrandom

import android.content.Context

/**
 * Manages the shared preferences for the application.
 *
 * @param context The application context
 */
class PreferenceManager(context: Context) {
    private companion object {
        const val PREF_SYSTEM_WALLPAPER = "SYSTEM_WALLPAPER"
        const val PREF_LOCK_SCREEN_WALLPAPER = "LOCK_SCREEN_WALLPAPER"
        const val PREF_DARK_THEME = "DARK_THEME"
    }

    /**
     * The preferences to load/store.
     */
    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    /**
     * If true, the system wallpaper should be set by the application.
     */
    var systemWallpaperEnabled: Boolean
        get() = prefs.getBoolean(PREF_SYSTEM_WALLPAPER, false)
        set(enabled) = prefs.edit().putBoolean(PREF_SYSTEM_WALLPAPER, enabled).apply()

    /**
     * If true, the lock-screen wallpaper should be set by the application.
     */
    var lockScreenWallpaperEnabled: Boolean
        get() = prefs.getBoolean(PREF_LOCK_SCREEN_WALLPAPER, false)
        set(enabled) = prefs.edit().putBoolean(PREF_LOCK_SCREEN_WALLPAPER, enabled).apply()

    /**
     * If true, the pictures will be generated in dark theme.
     */
    var darkTheme: Boolean
        get() = prefs.getBoolean(PREF_DARK_THEME, false)
        set(enabled) = prefs.edit().putBoolean(PREF_DARK_THEME, enabled).apply()
}