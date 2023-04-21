package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Color
import android.util.Size
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Interface for generating the lines of a Mondrian-style picture.
 */
abstract class LineGenerator(val canvasSize: Size) {
    /**
     * Generates the lines of a Mondrian-style picture.
     *
     * @param numLines The number of lines to generate
     */
    abstract fun generateLines(numLines: Int): List<Line>
}

/**
 * Generates the lines of a Mondrian-style picture in a random manner.
 *
 * @param canvasSize The size of the canvas
 * @param darkTheme If true, the lines will be generated in dark theme
 */
class RandomLineGenerator(canvasSize: Size, private val darkTheme: Boolean = false) : LineGenerator(canvasSize) {
    /**
     * The width of the lines
     */
    private val strokeWidth = canvasSize.width / 50

    /**
     * Generates the lines of a Mondrian-style picture in a random manner.
     *
     * @param numLines The number of lines to generate
     */
    override fun generateLines(numLines: Int): List<Line> {
        val color = if (darkTheme) Color.WHITE else Color.BLACK

        return buildList {
            add(
                Line(
                    LineAlignment.VERTICAL, 0, Pair(0, canvasSize.height), strokeWidth, color, false
                )
            ) // left
            add(
                Line(
                    LineAlignment.HORIZONTAL,
                    0,
                    Pair(0, canvasSize.width),
                    strokeWidth,
                    color,
                    false
                )
            ) // top
            add(
                Line(
                    LineAlignment.VERTICAL,
                    canvasSize.width,
                    Pair(0, canvasSize.height),
                    strokeWidth,
                    color,
                    false
                )
            ) // right
            add(
                Line(
                    LineAlignment.HORIZONTAL,
                    canvasSize.height,
                    Pair(0, canvasSize.width),
                    strokeWidth,
                    color,
                    false
                )
            ) // bottom

            for (i in 0 until numLines) {
                val alignment = getRandomLineAlignment(this, numLines)

                val parallelLinePositions =
                    filter { it.alignment == alignment }.map { it.fixCoordinate }
                val fixCoordinate = getRandomLinePosition(alignment, parallelLinePositions)

                val perpendicularLinePositions =
                    filter { it.alignment != alignment && it.dynamicCoordinates.first <= fixCoordinate && it.dynamicCoordinates.second >= fixCoordinate }

                val p1 = perpendicularLinePositions.random().fixCoordinate
                var p2: Int
                do {
                    p2 = perpendicularLinePositions.random().fixCoordinate
                } while (p1 == p2)

                val dynamicCoordinates = Pair(min(p1, p2), max(p1, p2))

                add(Line(alignment, fixCoordinate, dynamicCoordinates, strokeWidth, color))
            }
        }
    }

    /**
     * Gets a random alignment for the next line.
     * Maintains a minimum number of lines per orientation.
     *
     * @param lines The current lines
     * @param numLines The maximum number of lines
     */
    private fun getRandomLineAlignment(lines: List<Line>, numLines: Int): LineAlignment {
        val guarantee = { alignment: LineAlignment, minCount: Int ->
            val remaining = numLines - (lines.size - 4)
            if (remaining <= minCount && lines.count { it.visible && it.alignment == alignment } < minCount) alignment else null
        }

        return guarantee(LineAlignment.HORIZONTAL, 3) //
            ?: guarantee(LineAlignment.VERTICAL, 2) //
            ?: if (Random.nextBoolean()) LineAlignment.VERTICAL else LineAlignment.HORIZONTAL
    }

    /**
     * Gets a random position for the next line.
     * Maintains a minimum distance between lines.
     */
    private fun getRandomLinePosition(alignment: LineAlignment, existingLines: List<Int>): Int {
        var pos: Int

        do {
            pos = Random.nextInt(
                0, if (alignment == LineAlignment.VERTICAL) canvasSize.width else canvasSize.height
            )
        } while (existingLines.any { abs(it - pos) < strokeWidth * 5 })

        return pos
    }
}
