package hu.soma.veszelovszki.mondrianinrandom

/**
 * Adds a TAG variable to all the classes to simplify logging.
 * Usage:
 *     class MyClass {
 *         fun doSomething() {
 *             Log.d(TAG, "A log message")
 *         }
 *     }
 */
val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }