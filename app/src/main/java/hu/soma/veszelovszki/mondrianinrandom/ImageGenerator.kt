package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.createBitmap
import kotlin.random.Random

class ImageGenerator(
    private val lineGenerator: LineGenerator
) {
    private val bitmap =
        createBitmap(lineGenerator.canvasSize.width, lineGenerator.canvasSize.height)
    private val canvas = Canvas(bitmap)
    private val rectangles = mutableListOf(
        Rectangle(
            0,
            0,
            lineGenerator.canvasSize.width,
            lineGenerator.canvasSize.height
        )
    )

    fun generateImage(): Bitmap {
        canvas.drawColor(Color.WHITE)

        val numLines = Random.nextInt(4, 8)
        val lines = lineGenerator.generateLines(numLines)

        for (line in lines) {
            val newRectangles = mutableListOf<Rectangle>()

            for (rect in rectangles) {
                val croppedRect = rect.crop(line)
                if (!croppedRect.isEmpty()) {
                    newRectangles.add(croppedRect)
                }
            }

            rectangles.addAll(newRectangles)
        }

        assignFillColors()

        rectangles.forEach {
            canvas.drawRect(it)
        }

        lines.filter { it.visible }.forEach {
            canvas.drawLine(it)
        }

        return bitmap
    }

    private fun assignFillColors() {
        val numColoredRectangles = Random.nextInt(3, 6)
        val largestArea = rectangles.maxBy { it.area }.area

        for (i in 0 until numColoredRectangles) {
            val candidates = rectangles.filter { r1 ->
                r1.area < largestArea && r1.color == null && rectangles.none { r2 ->
                    r2.color != null && r1.hasCommonEdgeWith(r2)
                }
            }

            if (candidates.isEmpty()) {
                break
            }

            candidates.random().color = getNextFillColor()
        }
    }

    private fun getNextFillColor(): Int {
        val colors = listOf(Color.BLUE, Color.RED, Color.YELLOW)
        val colorDistribution = colors.associateWith { c -> rectangles.count { r -> r.color == c } }
        val minCount = colorDistribution.minBy { it.value }.value
        val candidates = colorDistribution.filterValues { it == minCount }.keys
        return candidates.random()
    }
}