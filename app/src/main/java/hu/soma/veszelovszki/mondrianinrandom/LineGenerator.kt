package hu.soma.veszelovszki.mondrianinrandom

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

interface LineGenerator {
    fun generateLines(numLines: Int): List<Line>
}

class RandomLineGenerator(
    private val width: Int, private val height: Int
) : LineGenerator {
    private val strokeWidth = width / 50

    override fun generateLines(numLines: Int): List<Line> {
        return buildList<Line> {

            add(Line(Alignment.VERTICAL, 0, Pair(0, height), strokeWidth, false)) // left
            add(Line(Alignment.HORIZONTAL, 0, Pair(0, width), strokeWidth, false)) // top
            add(Line(Alignment.VERTICAL, width, Pair(0, height), strokeWidth, false)) // right
            add(Line(Alignment.HORIZONTAL, height, Pair(0, width), strokeWidth, false)) // bottom

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

    private fun getNextLineAlignment(lines: List<Line>, numLines: Int): Alignment {
        val remaining = numLines - (lines.size - 4)
        if (remaining == 1) {
            if (lines.none { it.visible && it.alignment == Alignment.VERTICAL }) {
                return Alignment.VERTICAL
            }

            if (lines.none { it.visible && it.alignment == Alignment.HORIZONTAL }) {
                return Alignment.HORIZONTAL
            }
        }

        return if (Random.nextBoolean()) Alignment.VERTICAL else Alignment.HORIZONTAL
    }

    private fun getRandomLinePosition(alignment: Alignment, existingLines: List<Int>): Int {
        var pos: Int

        do {
            pos = Random.nextInt(0, if (alignment == Alignment.VERTICAL) width else height)
        } while (existingLines.any { abs(it - pos) < strokeWidth * 5 })

        return pos
    }
}
