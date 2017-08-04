package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.ScaledDoubleField
import uk.co.nickthecoder.paratask.util.uncamel

data class ScaledValue(var value: Double, var scale: Double = 1.0) {

    var scaledValue: Double
        get() = value * scale
        set(v) {
            value = v / scale
        }

    override fun toString() = "$value $scale"

    companion object {
        fun fromString(str: String): ScaledValue {
            val space = str.indexOf(' ')
            if (space < 0) {
                //println("Parsing unit $str")
                return ScaledValue(str.toDouble())
            } else {
                //println("Parsing ${str.substring(0, space)} and ${str.substring(space + 1)}")
                return ScaledValue(str.substring(0, space).toDouble(), str.substring(space + 1).toDouble())
            }
        }
    }
}

/**
 */
class ScaledDoubleParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: ScaledValue = ScaledValue(0.0, 1.0),
        scales: Map<String, Double>,
        val minValue: Double = 0.0,
        val maxValue: Double = Double.MAX_VALUE)

    : AbstractValueParameter<ScaledValue>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = true) {

    val scales = mutableMapOf<String, Double>()

    var scaleString: String
        get() = scales.filter { (_, scale) -> scale == value.scale }.map { (key, _) -> key }.firstOrNull() ?: ""
        set(v) {
            value.scale = scales[v] ?: 1.0
        }

    init {
        this.scales.putAll(scales)
    }

    override val converter = object : StringConverter<ScaledValue>() {
        override fun fromString(str: String): ScaledValue {

            try {
                return ScaledValue.fromString(str)
            } catch (e: Exception) {
                throw ParameterException(this@ScaledDoubleParameter, "Not a number")
            }
        }

        override fun toString(obj: ScaledValue): String {
            return obj.toString()
        }
    }

    override fun errorMessage(v: ScaledValue?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (v.scaledValue < minValue) {
            return "Cannot be less than ${minValue / v.scale}"
        } else if (v.scaledValue > maxValue) {
            return "Cannot be more than ${maxValue / v.scale}"
        }
        return null
    }

    override fun isStretchy() = false

    override fun toString(): String = "ScaledDouble" + super.toString()

    override fun copy(): ScaledDoubleParameter {
        return ScaledDoubleParameter(name, label, description, value, scales)
    }

    override fun createField() = ScaledDoubleField(this, ScaledDoubleParameterAdaptor(this))
}