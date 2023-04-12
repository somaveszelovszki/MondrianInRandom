package hu.soma.veszelovszki.mondrianinrandom

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.toSize
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val intent = Intent(this, SetWallpaperService::class.java)
//        intent.putExtra(SetWallpaperService.Options.CanvasWidth.name, displayMetrics.widthPixels)
//        intent.putExtra(SetWallpaperService.Options.CanvasHeight.name, displayMetrics.heightPixels)
        intent.putExtra(SetWallpaperService.Options.CanvasWidth.name, 640)
        intent.putExtra(SetWallpaperService.Options.CanvasHeight.name, 920)
        startService(intent)

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
                                    Size(coordinates.size.width, coordinates.size.height)
                                )
                                dynamicImagePainter.image = ImageGenerator(
                                    lineGenerator
                                )
                                    .generateImage()
                                    .asImageBitmap()
                            })

//                    Box(
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Text(
//                            text = "${getString(R.string.app_name)} is enabled.\nEnjoy the nice lock screen images!\nYou can close this application now.",
//                            textAlign = TextAlign.Center
//                        )
//
//
//                    }
                }
            }
        }
    }
}
