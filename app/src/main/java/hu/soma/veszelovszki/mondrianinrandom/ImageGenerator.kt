package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.*
import androidx.core.graphics.createBitmap
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.random.Random

enum class Alignment { HORIZONTAL, VERTICAL }

data class Rectangle(
    val left: Int, val top: Int, val right: Int, val bottom: Int, val color: Int
) {
    fun toRect(): Rect = Rect(left, top, right, bottom)
}

data class Line(
    val alignment: Alignment,
    val fixCoordinate: Int,
    val dynamicCoordinates: Pair<Int, Int>,
    val strokeWidth: Int,
    val visible: Boolean = true
) {
    val left
        get() = if (alignment == Alignment.VERTICAL) fixCoordinate else dynamicCoordinates.first

    val top
        get() = if (alignment == Alignment.HORIZONTAL) fixCoordinate else dynamicCoordinates.first

    val right
        get() = if (alignment == Alignment.VERTICAL) fixCoordinate else dynamicCoordinates.second

    val bottom
        get() = if (alignment == Alignment.HORIZONTAL) fixCoordinate else dynamicCoordinates.second
}

fun Canvas.drawRect(rect: Rectangle): Unit {
    val paint = Paint().apply {
        color = rect.color
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    drawRect(rect.toRect(), paint)
}

fun Canvas.drawLine(line: Line): Unit {
    val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = line.strokeWidth.toFloat()
    }

    drawLine(
        line.left.toFloat(), line.top.toFloat(), line.right.toFloat(), line.bottom.toFloat(), paint
    )
}

class ImageGenerator(private val width: Int, private val height: Int) {
    private val strokeWidth = width / 50

    private val bitmap = createBitmap(width, height)
    private val canvas = Canvas(bitmap)

    private val numLines = Random.nextInt(3, 8)

    private val lines = mutableListOf(
        Line(Alignment.VERTICAL, 0, Pair(0, height), strokeWidth, false), // left
        Line(Alignment.HORIZONTAL, 0, Pair(0, width), strokeWidth, false), // top
        Line(Alignment.VERTICAL, width, Pair(0, height), strokeWidth, false), // right
        Line(Alignment.HORIZONTAL, height, Pair(0, width), strokeWidth, false) // bottom
    )

    private val rectangles = mutableListOf(Rectangle(0, 0, width, height, Color.WHITE))

    fun generateImage(): Bitmap {
        canvas.drawColor(Color.WHITE)

        for (i in 0 until numLines) {
            val line = makeLine(getNextLineAlignment(i))
            lines.add(line)
        }

        lines.forEach {
            canvas.drawLine(it)
        }

        return bitmap
    }

    private fun makeLine(alignment: Alignment): Line {
        val parallelLinePositions =
            lines.filter { it.alignment == alignment }.map { it.fixCoordinate }
        val fixCoordinate = getRandomLinePosition(alignment, parallelLinePositions)

        val perpendicularLinePositions =
            lines.filter { it.alignment != alignment && it.dynamicCoordinates.first <= fixCoordinate && it.dynamicCoordinates.second >= fixCoordinate }

        val p1 = perpendicularLinePositions.random().fixCoordinate
        var p2 = 0
        do {
            p2 = perpendicularLinePositions.random().fixCoordinate
        } while (p1 == p2)

        val dynamicCoordinates = Pair(min(p1, p2), max(p1, p2))

        return Line(alignment, fixCoordinate, dynamicCoordinates, strokeWidth)
    }

    private fun getNextLineAlignment(index: Int): Alignment {
        if (index == numLines - 1) {
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
        var pos = 0

        do {
            pos = Random.nextInt(0, if (alignment == Alignment.VERTICAL) width else height)
        } while (existingLines.any { abs(it - pos) < strokeWidth * 5 })

        return pos
    }

    private fun getFillColor(): Int {
        return listOf(Color.BLUE, Color.RED, Color.YELLOW, Color.WHITE).random()
    }
}