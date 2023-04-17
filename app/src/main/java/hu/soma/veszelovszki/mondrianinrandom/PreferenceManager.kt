package hu.soma.veszelovszki.mondrianinrandom

import android.content.Context

class PreferenceManager(context: Context) {
    private companion object {
        const val PREF_SYSTEM_WALLPAPER = "SYSTEM_WALLPAPER"
        const val PREF_LOCK_SCREEN_WALLPAPER = "LOCK_SCREEN_WALLPAPER"
    }

    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    var systemWallpaperEnabled: Boolean
        get() = prefs.getBoolean(PREF_SYSTEM_WALLPAPER, false)
        set(enabled) = prefs.edit().putBoolean(PREF_SYSTEM_WALLPAPER, enabled).apply()

    var lockScreenWallpaperEnabled: Boolean
        get() = prefs.getBoolean(PREF_LOCK_SCREEN_WALLPAPER, false)
        set(enabled) = prefs.edit().putBoolean(PREF_LOCK_SCREEN_WALLPAPER, enabled).apply()
}