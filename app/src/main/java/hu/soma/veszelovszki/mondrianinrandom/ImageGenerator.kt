package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.random.Random

enum class Alignment { HORIZONTAL, VERTICAL }

data class Line(
    val strokeWidth: Int,
    val startX: Int,
    val startY: Int,
    val alignment: Alignment,
    val length: Int
) {}

fun Canvas.drawLine(line: Line): Unit {
    val stopX = if (line.alignment == Alignment.VERTICAL) line.startX else line.startX + line.length
    val stopY =
        if (line.alignment == Alignment.HORIZONTAL) line.startY else line.startY + line.length

    val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = line.strokeWidth.toFloat()
    }

    drawLine(line.startX.toFloat(), line.startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)
}

class ImageGenerator(private val width: Int, private val height: Int) {
    private val strokeWidth = width / 50

    fun generateImage(): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val frame = 10.0f
        canvas.drawColor(Color.BLACK)
        val paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
        canvas.drawRect(frame, frame, width - frame, height - frame, paint)

        val lines = getLines()

        lines.forEach {
            canvas.drawLine(it)
        }

        return bitmap
    }

    private fun getLines(): List<Line> {
        val horizontalStartPositions = getShuffledLinePositions(height)
        val verticalStartPositions = getShuffledLinePositions(width)

        return buildList {

            val addedHorizontalPositions = mutableListOf<Int>()
            val addedVerticalPositions = mutableListOf<Int>()

            while (horizontalStartPositions.isNotEmpty() || verticalStartPositions.isNotEmpty()) {
                if (verticalStartPositions.isNotEmpty() && (horizontalStartPositions.isEmpty() || Random.nextBoolean())) {
                    val startPosX = verticalStartPositions[0]

                    val (y1, y2) = if (addedHorizontalPositions.isEmpty()) Pair(
                        0,
                        height
                    ) else Pair(
                        if (Random.nextBoolean()) 0 else height, addedHorizontalPositions.random()
                    )

                    add(Line(strokeWidth, startPosX, min(y1, y2), Alignment.VERTICAL, abs(y1 - y2)))

                    verticalStartPositions.removeAt(0)
                    addedVerticalPositions.add(startPosX)
                } else {
                    val startPosY = horizontalStartPositions[0]

                    val (x1, x2) = if (addedVerticalPositions.isEmpty()) Pair(0, width) else Pair(
                        if (Random.nextBoolean()) 0 else width, addedVerticalPositions.random()
                    )

                    add(
                        Line(
                            strokeWidth, min(x1, x2), startPosY, Alignment.HORIZONTAL, abs(x1 - x2)
                        )
                    )

                    horizontalStartPositions.removeAt(0)
                    addedHorizontalPositions.add(startPosY)
                }
            }
        }
    }

    private fun getShuffledLinePositions(sectionLength: Int): MutableList<Int> {
        val numLines = Random.nextInt(1, 3)
        val positions = mutableListOf<Int>()

        for (i in 0 until numLines) {
            val prevPos = if (positions.isEmpty()) 0 else positions.last()
            val maxPos = sectionLength - (numLines - i) * strokeWidth * 5

            positions.add(
                Random.nextInt(
                    prevPos + strokeWidth * 5, maxPos
                )
            )
        }

        return positions.shuffled().toMutableList()
    }

    private fun getFillColor(): Int {
        return listOf(Color.BLUE, Color.RED, Color.YELLOW).random()
    }
}