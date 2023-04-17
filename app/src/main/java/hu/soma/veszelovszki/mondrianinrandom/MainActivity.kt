package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

data class Letter(@DrawableRes val resId: Int, val index: Float) {}

class MainActivity : ComponentActivity() {
    enum class MainPageState { Animation, FeatureSelector }

    enum class AnimationState { Mondrian, InRandom, Hidden, Finished }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    var state by remember { mutableStateOf(MainPageState.Animation) }

                    when (state) {
                        MainPageState.Animation -> AnimatedLetters(this, onAnimationFinished = {
                            state = MainPageState.FeatureSelector
                        })
                        MainPageState.FeatureSelector -> {
                            val prefs = PreferenceManager(this)

                            FeatureSelector(defaultSystemEnabled = prefs.systemWallpaperEnabled,
                                defaultLockScreenEnabled = prefs.lockScreenWallpaperEnabled,
                                onSaved = { systemEnabled, lockScreenEnabled ->
                                    prefs.systemWallpaperEnabled = systemEnabled
                                    prefs.lockScreenWallpaperEnabled = lockScreenEnabled

                                    schedulePeriodicSetWallpaperWorker(this)
                                    finishAndRemoveTask()

                                })
                        }
                    }
                }

            }
        }
    }
}
