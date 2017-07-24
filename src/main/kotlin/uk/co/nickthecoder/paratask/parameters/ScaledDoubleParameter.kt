package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.parameters.fields.ScaledDoubleField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 */
class ScaledDoubleParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Double? = null,
        required: Boolean = true,
        minValue: Double = 0.0,
        maxValue: Double = Double.MAX_VALUE,
        scales: Map<String, Double>,
        currentScale: Double = 1.0)

    : DoubleParameter(name, label, description, value, required, minValue, maxValue) {

    val scales = mutableMapOf<String, Double>()

    var currentScale: Double = currentScale

    var scaledValue
        get() = this.value?.times(currentScale)
        set(v) {
            value = v?.div(currentScale)
        }

    var scaleString: String?
        get() = scales.filter { (_, scale) -> scale == currentScale }.map { (key, _) -> key }.firstOrNull()
        set(v) {
            currentScale = scales[v] ?: 1.0
        }

    init {
        this.scales.putAll(scales)
    }

    override val converter = object : StringConverter<Double?>() {
        override fun fromString(str: String): Double? {
            val trimmed = str.trim()

            if (trimmed.isEmpty()) {
                return null
            }
            var nString = trimmed
            var scale = 1.0
            scales.filter { (str, _) ->
                trimmed.endsWith(str)
            }.forEach { (str, aScale) ->
                nString = trimmed.substring(0, trimmed.length - str.length).trim()
                scale = aScale
            }
            try {
                return nString.toDouble() * scale
            } catch (e: Exception) {
                throw ParameterException(this@ScaledDoubleParameter, "Not a number")
            }
        }

        override fun toString(obj: Double?): String {
            if (obj == null) {
                return ""
            } else {
                val str = scaleString
                if (str == null) {
                    // Current scale wasn't found in the map, so lets ignore it
                    return obj.toString()
                } else {
                    return "${(obj / currentScale).toString()} $str"
                }
            }
        }

    }

    override fun createField() = ScaledDoubleField(this)
}
