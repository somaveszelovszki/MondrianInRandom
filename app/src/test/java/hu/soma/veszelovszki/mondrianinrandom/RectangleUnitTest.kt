package hu.soma.veszelovszki.mondrianinrandom

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RectangleUnitTest {
    @Test
    fun rectangle_hasCommonEdgeWith_notTouching() {
        val r1 = Rectangle(10, 10, 100, 100)
        val r2 = Rectangle(101, 101, 200, 200)

        assertFalse(r1.hasCommonEdgeWith(r2))
        assertFalse(r2.hasCommonEdgeWith(r1))
    }

    @Test
    fun rectangle_hasCommonEdgeWith_cornersTouching() {
        val r1 = Rectangle(10, 10, 100, 100)
        val r2 = Rectangle(100, 100, 200, 200)

        assertFalse(r1.hasCommonEdgeWith(r2))
        assertFalse(r2.hasCommonEdgeWith(r1))
    }

    @Test
    fun rectangle_hasCommonEdgeWith_commonVerticalEdge() {
        val r1 = Rectangle(10, 10, 100, 100)
            val r2 = Rectangle(100, 50, 200, 200)

        assertTrue(r1.hasCommonEdgeWith(r2))
        assertTrue(r2.hasCommonEdgeWith(r1))
    }

    @Test
    fun rectangle_hasCommonEdgeWith_verticalEdgeSubset() {
        val r1 = Rectangle(10, 10, 100, 100)
        val r2 = Rectangle(100, 5, 200, 200)

        assertTrue(r1.hasCommonEdgeWith(r2))
        assertTrue(r2.hasCommonEdgeWith(r1))
    }

    @Test
    fun rectangle_hasCommonEdgeWith_commonHorizontalEdge() {
        val r1 = Rectangle(10, 10, 100, 100)
        val r2 = Rectangle(50, 100, 200, 200)

        assertTrue(r1.hasCommonEdgeWith(r2))
        assertTrue(r2.hasCommonEdgeWith(r1))
    }

    @Test
    fun rectangle_hasCommonEdgeWith_horizontalEdgeSubset() {
        val r1 = Rectangle(10, 10, 100, 100)
        val r2 = Rectangle(5, 100, 200, 200)

        assertTrue(r1.hasCommonEdgeWith(r2))
        assertTrue(r2.hasCommonEdgeWith(r1))
    }
}