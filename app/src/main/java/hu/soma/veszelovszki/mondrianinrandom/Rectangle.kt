package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.abs

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

fun Canvas.drawRect(rect: Rectangle): Unit {
    val paint = Paint().apply {
        color = rect.color ?: Color.WHITE
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    drawRect(rect.toRect(), paint)
}
