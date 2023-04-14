package hu.soma.veszelovszki.mondrianinrandom

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

val MAIN_PAGE_LETTERS = listOf(
    R.drawable.m,
    R.drawable.o,
    R.drawable.n,
    R.drawable.d,
    R.drawable.r,
    R.drawable.i,
    R.drawable.a,
    R.drawable.n
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        schedulePeriodicSetWallpaperWorker(this)

        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    AnimatedLetters()
                }
            }
        }
    }

    @Composable
    fun AnimatedLetters() {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            for (i in MAIN_PAGE_LETTERS.indices) {
                drawLetter(i, size, resources)
            }
        })
    }
}

fun DrawScope.drawLetter(
    letterIdx: Int, canvasSize: Size, resources: Resources
) {
    val spacingRatio = 0.2f
    val normalizedWidth = MAIN_PAGE_LETTERS.size + (MAIN_PAGE_LETTERS.size - 1) * spacingRatio

    val contentWidth = canvasSize.width * 0.8f
    val leftBorder = (canvasSize.width - contentWidth) / 2
    val horizontalCenter = canvasSize.height / 2

    val imageSize = contentWidth / normalizedWidth
    val spacing = imageSize * spacingRatio

    val image = (resources.getDrawable(
        MAIN_PAGE_LETTERS[letterIdx], null
    ) as BitmapDrawable).bitmap.let { bitmap ->
        Bitmap.createScaledBitmap(
            bitmap, imageSize.toInt(), imageSize.toInt(), true
        )
    }

    drawImage(
        image.asImageBitmap(), Offset(
            leftBorder + letterIdx * (imageSize + spacing), horizontalCenter - imageSize / 2
        )
    )
}