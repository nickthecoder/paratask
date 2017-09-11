package uk.co.nickthecoder.paratask.parameters.compound

/**
 * Holds a double value with units,
 * For example, store a length, with units of "mm" -> 1.0, "cm" -> 10.0 and "m" -> 1000.0
 * A value of 3 meters is stored using : amount = 3, and a scale = 1000.0. Value = 3000.0.
 */
data class ScaledDouble(
        var amount: Double,
        var scale: Double = 1.0,
        val scales: Map<Double, String>) {

    var value: Double
        get() = amount * scale
        set(v) {
            amount = v / scale
        }

    override fun toString() = "$amount ${scales[scale]}"

}
