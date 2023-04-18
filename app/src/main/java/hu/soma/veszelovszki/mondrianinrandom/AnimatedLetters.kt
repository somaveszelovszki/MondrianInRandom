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
import androidx.core.content.res.ResourcesCompat

/**
 * Represents a letter animation state.
 */
enum class LetterAnimationState { Mondrian, InRandom }

/**
 * Represents an index-based item in an image list.
 *
 * @param resId The id of the image resource
 * @param index The index of the item in the list
 */
data class ImageListItem(@DrawableRes val resId: Int, val index: Float) {}

/**
 * Displays an animation in which the text 'MONDRIAN' is transitioned into 'IN RANDOM'.
 *
 * @param context The application context
 * @param onAnimationFinished The callback to be invoked when the animation is finished
 */
@Composable
fun AnimatedLetters(context: Context, onAnimationFinished: ((Unit) -> Unit)? = null) {
    var state by remember { mutableStateOf(LetterAnimationState.Mondrian) }
    val transition = updateTransition(state, "Transition")

    val tweenPosition = { tween<Float>(delayMillis = 2000, durationMillis = 1500) }

    val animatedIndex: @Composable (Int, Int) -> Float = { start: Int, end: Int ->
        transition.animateFloat(
            transitionSpec = { tweenPosition() }, label = "Letter Vertical Position Transition"
        ) {
            if (it == LetterAnimationState.Mondrian) start.toFloat() else end.toFloat()
        }.value
    }

    val horizontalCenter by transition.animateFloat(
        transitionSpec = { tweenPosition() }, label = "Letter Horizontal Position Transition"
    ) {
        if (it == LetterAnimationState.Mondrian) 0.45f else 0.55f
    }

    val letters = listOf(
        ImageListItem(R.drawable.m, animatedIndex(0, 7)),
        ImageListItem(R.drawable.o, animatedIndex(1, 6)),
        ImageListItem(R.drawable.n, animatedIndex(2, 1)),
        ImageListItem(R.drawable.d, animatedIndex(3, 5)),
        ImageListItem(R.drawable.r, animatedIndex(4, 2)),
        ImageListItem(R.drawable.i, animatedIndex(5, 0)),
        ImageListItem(R.drawable.a, animatedIndex(6, 3)),
        ImageListItem(R.drawable.n, animatedIndex(7, 4))
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            letters.forEach { drawImageListItem(it, context.resources, horizontalCenter) }
        })
    }

    when (state) {
        LetterAnimationState.Mondrian -> LaunchedEffect(Unit) {
            state = LetterAnimationState.InRandom
        }
        LetterAnimationState.InRandom -> if (transition.currentState == transition.targetState) {
            onAnimationFinished?.invoke(Unit)
        }
    }
}

/**
 * Draws an image on the canvas to a position defined by its index.
 *
 * @param item The image list item descriptor
 * @param resources The context-dependent resources
 * @param horizontalCenter The horizontal center position for the image
 */
fun DrawScope.drawImageListItem(
    item: ImageListItem, resources: Resources, horizontalCenter: Float
) {
    val contentWidth = 0.8f * size.width
    val imageWidth = 0.08f * size.width
    val spacingWidth = (contentWidth - 8 * imageWidth) / 7

    val image = (ResourcesCompat.getDrawable(
        resources,
        item.resId,
        null
    ) as BitmapDrawable).bitmap.let { bitmap ->
        Bitmap.createScaledBitmap(
            bitmap, imageWidth.toInt(), imageWidth.toInt(), true
        )
    }

    drawImage(
        image.asImageBitmap(), Offset(
            (size.width - contentWidth) / 2 + (imageWidth + spacingWidth) * item.index,
            horizontalCenter * size.height - imageWidth / 2
        )
    )
}