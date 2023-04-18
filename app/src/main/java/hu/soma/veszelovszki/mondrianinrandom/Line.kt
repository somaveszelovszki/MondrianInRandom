package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Represents a line alignment.
 */
enum class LineAlignment { HORIZONTAL, VERTICAL }

/**
 * Represents a horizontal or vertical line in a Mondrian-style picture
 *
 * @param alignment The alignment of the line
 * @param fixCoordinate If the line is vertical, stores the X coordinate, Y otherwise
 * @param dynamicCoordinates If the line is vertical, stores the top and bottom, otherwise left and right
 * @param strokeWidth The width of the line
 * @param visible If true, the line is visible
 */
data class Line(
    val alignment: LineAlignment,
    val fixCoordinate: Int,
    val dynamicCoordinates: Pair<Int, Int>,
    val strokeWidth: Int,
    val visible: Boolean = true
) {
    val left
        get() = if (alignment == LineAlignment.VERTICAL) fixCoordinate else dynamicCoordinates.first

    val top
        get() = if (alignment == LineAlignment.HORIZONTAL) fixCoordinate else dynamicCoordinates.first

    val right
        get() = if (alignment == LineAlignment.VERTICAL) fixCoordinate else dynamicCoordinates.second

    val bottom
        get() = if (alignment == LineAlignment.HORIZONTAL) fixCoordinate else dynamicCoordinates.second
}

/**
 * Draws a line on the canvas.
 *
 * @param line The line
 */
fun Canvas.drawLine(line: Line) {
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
