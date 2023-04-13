package hu.soma.veszelovszki.mondrianinrandom

import android.util.Size
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

abstract class LineGenerator(val canvasSize: Size) {
    abstract fun generateLines(numLines: Int): List<Line>
}

class RandomLineGenerator(canvasSize: Size) : LineGenerator(canvasSize) {
    private val strokeWidth = canvasSize.width / 50

    override fun generateLines(numLines: Int): List<Line> {
        return buildList {

            add(Line(LineAlignment.VERTICAL, 0, Pair(0, canvasSize.height), strokeWidth, false)) // left
            add(Line(LineAlignment.HORIZONTAL, 0, Pair(0, canvasSize.width), strokeWidth, false)) // top
            add(
                Line(
                    LineAlignment.VERTICAL,
                    canvasSize.width,
                    Pair(0, canvasSize.height),
                    strokeWidth,
                    false
                )
            ) // right
            add(
                Line(
                    LineAlignment.HORIZONTAL,
                    canvasSize.height,
                    Pair(0, canvasSize.width),
                    strokeWidth,
                    false
                )
            ) // bottom

            for (i in 0 until numLines) {
                val alignment = getNextLineAlignment(this, numLines)

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

                add(Line(alignment, fixCoordinate, dynamicCoordinates, strokeWidth))
            }
        }
    }

    private fun getNextLineAlignment(lines: List<Line>, numLines: Int): LineAlignment {
        val minAligned = 2
        val remaining = numLines - (lines.size - 4)
        if (remaining <= minAligned) {
            if (lines.count { it.visible && it.alignment == LineAlignment.VERTICAL } < minAligned) {
                return LineAlignment.VERTICAL
            }

            if (lines.count { it.visible && it.alignment == LineAlignment.HORIZONTAL } < minAligned) {
                return LineAlignment.HORIZONTAL
            }
        }

        return if (Random.nextBoolean()) LineAlignment.VERTICAL else LineAlignment.HORIZONTAL
    }

    private fun getRandomLinePosition(alignment: LineAlignment, existingLines: List<Int>): Int {
        var pos: Int

        do {
            pos = Random.nextInt(
                0,
                if (alignment == LineAlignment.VERTICAL) canvasSize.width else canvasSize.height
            )
        } while (existingLines.any { abs(it - pos) < strokeWidth * 5 })

        return pos
    }
}
