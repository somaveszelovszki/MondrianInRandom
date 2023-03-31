package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.*
import androidx.core.graphics.createBitmap
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.random.Random

enum class Alignment { HORIZONTAL, VERTICAL }

data class Rectangle(
    var left: Int = 0,
    var top: Int = 0,
    var right: Int = 0,
    var bottom: Int = 0,
    var color: Int? = null
) {
    fun toRect(): Rect = Rect(left, top, right, bottom)

    fun isEmpty() = left >= right || top >= bottom

    val area
        get() = if (isEmpty()) 0 else (right - left) * (bottom - top)

    fun crop(line: Line): Rectangle {
        if (line.alignment == Alignment.VERTICAL) {
            return if (line.fixCoordinate <= left || line.fixCoordinate >= right || line.top >= bottom || line.bottom <= top) {
                Rectangle()
            } else {
                val cropLeft = abs(line.fixCoordinate - left) < abs(line.fixCoordinate - right)

                val cropped = Rectangle(
                    if (cropLeft) left else line.fixCoordinate,
                    top,
                    if (cropLeft) line.fixCoordinate else right,
                    bottom,
                    color
                )

                if (cropLeft) {
                    left = line.fixCoordinate
                } else {
                    right = line.fixCoordinate
                }

                cropped
            }
        } else {
            return if (line.fixCoordinate <= top || line.fixCoordinate >= bottom || line.right <= left || line.left >= right) {
                Rectangle()
            } else {
                val cropTop = abs(line.fixCoordinate - top) < abs(line.fixCoordinate - bottom)

                val cropped = Rectangle(
                    left,
                    if (cropTop) top else line.fixCoordinate,
                    right,
                    if (cropTop) line.fixCoordinate else bottom,
                    color
                )

                if (cropTop) {
                    top = line.fixCoordinate
                } else {
                    bottom = line.fixCoordinate
                }

                cropped
            }

        }
    }

    fun hasCommonEdgeWith(other: Rectangle): Boolean {
        return if (top == other.bottom || bottom == other.top) {
            (left > other.left && left < other.right) || (right > other.left && right < other.right)
        } else if (left == other.right || right == other.left) {
            (top > other.top && top < other.bottom) || (bottom > other.top && bottom < other.bottom)
        } else {
            false
        }
    }
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
        color = rect.color ?: Color.WHITE
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

    private val numLines = Random.nextInt(3, 4)

    private val lines = mutableListOf(
        Line(Alignment.VERTICAL, 0, Pair(0, height), strokeWidth, false), // left
        Line(Alignment.HORIZONTAL, 0, Pair(0, width), strokeWidth, false), // top
        Line(Alignment.VERTICAL, width, Pair(0, height), strokeWidth, false), // right
        Line(Alignment.HORIZONTAL, height, Pair(0, width), strokeWidth, false) // bottom
    )

    private val rectangles = mutableListOf(Rectangle(0, 0, width, height))

    fun generateImage(): Bitmap {
        canvas.drawColor(Color.WHITE)

        for (i in 0 until numLines) {
            val line = makeLine(getNextLineAlignment(i))
            lines.add(line)

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

    private fun assignFillColors(): Unit {
        val colors = listOf(Color.BLUE, Color.RED, Color.YELLOW)
        val numColoredRectangles = Random.nextInt(2, 5)
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

            candidates.random().color = colors.random()
        }
    }
}