package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
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
            0, 0, lineGenerator.canvasSize.width, lineGenerator.canvasSize.height
        )
    )

    fun generateImage(): Bitmap {
        canvas.drawColor(Color.WHITE)

        val numVisibleLines = Random.nextInt(5, 9)
        val allLines = lineGenerator.generateLines(numVisibleLines)
        val visibleLines = allLines.filter { it.visible }

        for (line in allLines) {
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

        visibleLines.forEach {
            canvas.drawLine(it)
        }

        return bitmap
    }

    private fun assignFillColors() {
        val numColoredRectangles = Random.nextInt(3, 6)

        for (i in 0 until numColoredRectangles) {
            val candidates = rectangles.filter { r1 ->
                r1.color == null && rectangles.none { r2 ->
                    r2.color != null && r1.hasCommonEdgeWith(r2)
                }
            }

            if (candidates.isEmpty()) {
                break
            }

            val rect = candidates.random()
            rect.color = getNextFillColor(rect.area)
        }
    }

    private fun getNextFillColor(area: Int): Int {
        val colors = buildList {
            add(Color.RED)
            add(Color.YELLOW)
            add(Color.BLUE)

            if (area < lineGenerator.canvasSize.width * lineGenerator.canvasSize.height / 16) {
                add(Color.BLACK)
            }
        }

        val colorDistribution = colors.associateWith { c -> rectangles.count { r -> r.color == c } }
        val minCount = colorDistribution.minBy { it.value }.value
        val candidates = colorDistribution.filterValues { it == minCount }.keys
        return candidates.random()
    }
}