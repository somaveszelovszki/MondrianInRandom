package hu.soma.veszelovszki.mondrianinrandom

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

class LockScreenActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()

        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val dynamicImagePainter = DynamicImagePainter()

                    Image(painter = dynamicImagePainter,
                        contentDescription = "Generated Mondrian image",
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                val lineGenerator = RandomLineGenerator(
                                    coordinates.size.width, coordinates.size.height
                                )
                                dynamicImagePainter.image = ImageGenerator(
                                    coordinates.size.width, coordinates.size.height, lineGenerator
                                )
                                    .generateImage()
                                    .asImageBitmap()
                            }
                            .clickable { finishAndRemoveTask() })
                }
            }
        }

        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        finishAndRemoveTask()
    }

    private fun hideSystemUI() {
        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }
}
