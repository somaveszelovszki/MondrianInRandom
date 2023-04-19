package hu.soma.veszelovszki.mondrianinrandom

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlin.math.roundToInt

class DynamicImagePainter() : Painter() {
    var image: ImageBitmap? = null

    override fun DrawScope.onDraw() {
        image?.apply {
            drawImage(
                image!!, dstSize = IntSize(
                    this@onDraw.size.width.roundToInt(), this@onDraw.size.height.roundToInt()
                )
            )
        }
    }

    /**
     * Return the dimension of the underlying [ImageBitmap] as it's intrinsic width and height
     */
    override val intrinsicSize: Size
        get() = if (image != null) IntSize(
            image!!.width, image!!.height
        ).toSize() else Size(0.0f, 0.0f)
}
