package hu.soma.veszelovszki.mondrianinrandom

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

data class Letter(@DrawableRes val resId: Int, val index: Float) {}

class MainActivity : ComponentActivity() {
    enum class MainPageState { Animation, InfoText }

    enum class AnimationState { Mondrian, InRandom, Hidden, Finished }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        schedulePeriodicSetWallpaperWorker(this)

        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    var state by remember { mutableStateOf(MainPageState.Animation) }

                    when (state) {
                        MainPageState.Animation -> AnimatedLetters(onAnimationFinished = {
                            state = MainPageState.InfoText
                        })
                        MainPageState.InfoText -> InfoText()
                    }


                }
            }
        }
    }

    @Composable
    fun AnimatedLetters(onAnimationFinished: ((Unit) -> Unit)? = null) {
        var state by remember { mutableStateOf(AnimationState.Mondrian) }
        val transition = updateTransition(state, "Transition")

        val tweenPosition = { tween<Float>(delayMillis = 2000, durationMillis = 1500) }
        val tweenAlpha = { tween<Float>(delayMillis = 1000, durationMillis = 1500) }

        val animatedIndex: @Composable (Int, Int) -> Float = { start: Int, end: Int ->
            transition.animateFloat(
                transitionSpec = { tweenPosition() }, label = "Letter Vertical Position Transition"
            ) {
                if (it == AnimationState.Mondrian) start.toFloat() else end.toFloat()
            }.value
        }

        val letters = listOf(
            Letter(R.drawable.m, animatedIndex(0, 7)),
            Letter(R.drawable.o, animatedIndex(1, 6)),
            Letter(R.drawable.n, animatedIndex(2, 1)),
            Letter(R.drawable.d, animatedIndex(3, 5)),
            Letter(R.drawable.r, animatedIndex(4, 2)),
            Letter(R.drawable.i, animatedIndex(5, 0)),
            Letter(R.drawable.a, animatedIndex(6, 3)),
            Letter(R.drawable.n, animatedIndex(7, 4))
        )

        val horizontalCenter by transition.animateFloat(
            transitionSpec = { tweenPosition() }, label = "Letter Horizontal Position Transition"
        ) {
            if (it == AnimationState.Mondrian) 0.45f else 0.55f
        }

        val alpha by transition.animateFloat(
            transitionSpec = { tweenAlpha() }, label = "Letter Alpha Transition"
        ) {
            if (it == AnimationState.Mondrian || it == AnimationState.InRandom) 1.0f else 0.0f
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                letters.forEach { drawLetter(it, resources, horizontalCenter) }
            })
        }

        val finished = transition.currentState == transition.targetState

        when (state) {
            AnimationState.Mondrian -> LaunchedEffect(Unit) {
                state = AnimationState.InRandom
            }
            AnimationState.InRandom -> if (finished) {
                state = AnimationState.Hidden
            }
            AnimationState.Hidden -> if (finished) {
                state = AnimationState.Finished
            }
            AnimationState.Finished -> onAnimationFinished?.invoke(Unit)
        }
    }

    private fun getFirstTextInfoParagraph() = buildAnnotatedString {
        val name = "Piet Mondrian"
        val text =
            "A random-generated picture in the style of $name has been set as your lock-screen wallpaper."

        val start = text.indexOf(name)
        val end = start + name.length

        append(text)
        addStyle(
            style = SpanStyle(
                color = Color.Blue, textDecoration = TextDecoration.Underline
            ), start = start, end = end
        )

        addStringAnnotation(
            tag = "URL",
            annotation = "https://en.wikipedia.org/wiki/Piet_Mondrian",
            start = start,
            end = end
        )
    }

    @Preview
    @Composable
    fun InfoText() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val textStyle =
                TextStyle(textAlign = TextAlign.Center, fontSize = 22.sp, color = Color.DarkGray)
            val modifier = Modifier.padding(25.dp)

            val showText: @Composable (String) -> Unit = { text ->
                Text(
                    text,
                    textAlign = textStyle.textAlign,
                    fontSize = textStyle.fontSize,
                    color = textStyle.color,
                    modifier = modifier
                )
            }

            getFirstTextInfoParagraph().let { text ->
                ClickableText(text, style = textStyle, modifier = modifier, onClick = {
                    text.getStringAnnotations("URL", it, it).firstOrNull()?.let { url ->
                        Log.i(TAG, "Opening webpage: ${url.item}")
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.item)))
                    }
                })
            }

            showText("It will automatically update every day at midnight.")
            showText("You can close this window now.")

            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.DarkGray,
                    contentColor = Color.White
                ), onClick = { finishAndRemoveTask() }) {
                Text("CLOSE")
            }
        }
    }
}

fun DrawScope.drawLetter(letter: Letter, resources: Resources, horizontalCenter: Float) {
    val contentWidth = 0.8f * size.width
    val imageWidth = 0.08f * size.width
    val spacingWidth = (contentWidth - 8 * imageWidth) / 7

    val image = (resources.getDrawable(
        letter.resId, null
    ) as BitmapDrawable).bitmap.let { bitmap ->
        Bitmap.createScaledBitmap(
            bitmap, imageWidth.toInt(), imageWidth.toInt(), true
        )
    }

    drawImage(
        image.asImageBitmap(), Offset(
            (size.width - contentWidth) / 2 + (imageWidth + spacingWidth) * letter.index,
            horizontalCenter * size.height - imageWidth / 2
        )
    )
}