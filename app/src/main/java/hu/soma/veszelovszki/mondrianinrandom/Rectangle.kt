package hu.soma.veszelovszki.mondrianinrandom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.abs

/**
 * Represents a colored rectangle.
 *
 * @param left The left coordinate
 * @param top The top coordinate
 * @param right The right coordinate
 * @param bottom The bottom coordinate
 * @param color The color of the rectangle
 */
data class Rectangle(
    var left: Int,
    var top: Int,
    var right: Int,
    var bottom: Int,
    var color: Int
) {
    /**
     * Converts the rectangle to a Rect type.
     * @return The rectangle as a Rect
     */
    fun toRect(): Rect = Rect(left, top, right, bottom)

    /**
     * Checks if the rectangle is empty.
     * @return True if the rectangle is empty, false otherwise
     */
    fun isEmpty() = left >= right || top >= bottom

    /**
     * The area of the rectangle.
     */
    val area
        get() = if (isEmpty()) 0 else (right - left) * (bottom - top)

    /**
     * Crops the rectangle by a line.
     * If the line intersects the rectangle, the slice with the bigger area is kept, the other is returned.
     * If the line does not intersects the rectangle, an empty rectangle is returned.
     *
     * @param line The line
     * @return The smaller slice after the cropping
     */
    fun crop(line: Line): Rectangle {
        if (line.alignment == LineAlignment.VERTICAL) {
            return if (line.fixCoordinate <= left || line.fixCoordinate >= right || line.top >= bottom || line.bottom <= top) {
                Rectangle(0, 0, 0, 0, Color.WHITE)
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
                Rectangle(0, 0, 0, 0, Color.WHITE)
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

    /**
     * Checks if the two rectangles have a common edge.
     *
     * @param other The other rectangle
     * @return True if the two rectangles have a common edge, false otherwise
     */
    fun hasCommonEdgeWith(other: Rectangle): Boolean {
        return if (top == other.bottom || bottom == other.top) {
            left < other.right && right > other.left
        } else if (left == other.right || right == other.left) {
            top < other.bottom && bottom > other.top
        } else {
            false
        }
    }
}

/**
 * Draw a rectangle on the canvas.
 *
 * @param rect The rectangle
 */
fun Canvas.drawRect(rect: Rectangle) {
    val paint = Paint().apply {
        color = rect.color
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    drawRect(rect.toRect(), paint)
}
