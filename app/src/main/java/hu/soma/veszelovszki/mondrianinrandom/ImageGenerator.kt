package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.createBitmap
import kotlin.random.Random

/**
 * Interface for generating a Mondrian-style picture.
 */
abstract class ImageGenerator() {
    /**
     * Generates a Mondrian-style picture.
     */
    abstract fun generateImage(): Bitmap
}

/**
 * Generates a Mondrian-style picture in a random manner.
 *
 * @param lineGenerator A line generator
 */
class RandomImageGenerator(
    private val lineGenerator: LineGenerator
): ImageGenerator() {
    /**
     * The target bitmap
     */
    private val bitmap =
        createBitmap(lineGenerator.canvasSize.width, lineGenerator.canvasSize.height)

    /**
     * The target canvas
     */
    private val canvas = Canvas(bitmap)

    /**
     * The rectangles that will be drawn between the generated lines
     */
    private val rectangles = mutableListOf(
        Rectangle(
            0, 0, lineGenerator.canvasSize.width, lineGenerator.canvasSize.height
        )
    )

    /**
     * Generates a Mondrian-style picture in a random manner.
     *
     * @return The generated bitmap
     */
    override fun generateImage(): Bitmap {
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

        assignRandomFillColors()

        rectangles.forEach {
            canvas.drawRect(it)
        }

        visibleLines.forEach {
            canvas.drawLine(it)
        }

        return bitmap
    }

    /**
     * Assigns random fill colors to the rectangles.
     */
    private fun assignRandomFillColors() {
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
            rect.color = getRandomFillColor(rect.area)
        }
    }

    /**
     * Gets a random fill color for a rectangle.
     * Maintains an even distribution of the colors.
     */
    private fun getRandomFillColor(area: Int): Int {
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