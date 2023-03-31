package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

enum class Alignment { HORIZONTAL, VERTICAL }

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
