package hu.soma.veszelovszki.mondrianinrandom

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope

data class Letter(@DrawableRes val resId: Int, val index: Float) {}

@Composable
fun AnimatedLetters(context: Context, onAnimationFinished: ((Unit) -> Unit)? = null) {
    var state by remember { mutableStateOf(MainActivity.AnimationState.Mondrian) }
    val transition = updateTransition(state, "Transition")

    val tweenPosition = { tween<Float>(delayMillis = 2000, durationMillis = 1500) }
    val tweenAlpha = { tween<Float>(delayMillis = 1000, durationMillis = 1500) }

    val animatedIndex: @Composable (Int, Int) -> Float = { start: Int, end: Int ->
        transition.animateFloat(
            transitionSpec = { tweenPosition() }, label = "Letter Vertical Position Transition"
        ) {
            if (it == MainActivity.AnimationState.Mondrian) start.toFloat() else end.toFloat()
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
        if (it == MainActivity.AnimationState.Mondrian) 0.45f else 0.55f
    }

    val alpha by transition.animateFloat(
        transitionSpec = { tweenAlpha() }, label = "Letter Alpha Transition"
    ) {
        if (it == MainActivity.AnimationState.Mondrian || it == MainActivity.AnimationState.InRandom) 1.0f else 0.0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            letters.forEach { drawLetter(it, context.resources, horizontalCenter) }
        })
    }

    val finished = transition.currentState == transition.targetState

    when (state) {
        MainActivity.AnimationState.Mondrian -> LaunchedEffect(Unit) {
            state = MainActivity.AnimationState.InRandom
        }
        MainActivity.AnimationState.InRandom -> if (finished) {
            state = MainActivity.AnimationState.Hidden
        }
        MainActivity.AnimationState.Hidden -> if (finished) {
            state = MainActivity.AnimationState.Finished
        }
        MainActivity.AnimationState.Finished -> onAnimationFinished?.invoke(Unit)
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