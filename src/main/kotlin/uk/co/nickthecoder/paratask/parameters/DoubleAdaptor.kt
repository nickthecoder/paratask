package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter

/**
 * Allows DoubleField to be used by DoubleParameter and ScaledDoubleParameter
 */
interface DoubleAdaptor {
    var value: Double?

    val converter: StringConverter<Double?>

    fun errorMessage(v: Double?): String?

    fun initialValue(): Double?

    val minValue: Double

    val maxValue: Double

    val valueProperty: SimpleObjectProperty<Double?>
}

class DoubleParameterAdaptor(val doubleParameter: DoubleParameter) : DoubleAdaptor {

    override var value: Double?
        get() = doubleParameter.value
        set(v) {
            doubleParameter.value = v
        }

    override val converter: StringConverter<Double?>
        get() = doubleParameter.converter

    override fun errorMessage(v: Double?) = doubleParameter.errorMessage(v)

    override fun initialValue(): Double? {
        if (doubleParameter.value == null && doubleParameter.required) {
            if (doubleParameter.minValue >= 0.0) {
                return doubleParameter.minValue
            } else if (doubleParameter.maxValue <= 0.0) {
                return doubleParameter.maxValue
            } else {
                return 0.0
            }

        } else {
            return doubleParameter.value
        }
    }

    override val minValue: Double = doubleParameter.minValue

    override val maxValue: Double = doubleParameter.maxValue

    override val valueProperty: SimpleObjectProperty<Double?> = doubleParameter.valueProperty

}

class ScaledDoubleParameterAdaptor(val scaledDoubleParameter: ScaledDoubleParameter) : DoubleAdaptor {

    override var value: Double?
        get() = scaledDoubleParameter.value.value
        set(v) {
            scaledDoubleParameter.value.value = v!!
        }

    override val converter = object : StringConverter<Double?>() {
        override fun fromString(string: String): Double? = string.toDouble()
        override fun toString(obj: Double?): String = obj.toString()
    }

    override fun errorMessage(v: Double?) = null

    override fun initialValue(): Double = scaledDoubleParameter.value.value

    override val minValue: Double = Double.MIN_VALUE

    override val maxValue: Double = Double.MAX_VALUE


    override var valueProperty = object : SimpleObjectProperty<Double?>() {
        override fun set(v: Double?) {
            val changed = v != get()
            if (changed) {
                super.set(v)
                scaledDoubleParameter.value.value = v!!
                scaledDoubleParameter.parameterListeners.fireValueChanged(scaledDoubleParameter)
            }
        }
        override fun get() = scaledDoubleParameter.value.value
    }
}
