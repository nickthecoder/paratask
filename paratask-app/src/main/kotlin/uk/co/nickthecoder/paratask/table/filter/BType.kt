package uk.co.nickthecoder.paratask.table.filter

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter
import uk.co.nickthecoder.paratask.parameters.compound.TemporalAmountParameter
import java.time.LocalDate
import java.time.temporal.TemporalAmount

/**
 * Defines the second argument for a [Test] to be used within a [RowFilter].
 * It is call BType, because [Test.accept] takes two parameters named "a" and "b".
 * "a" is taken from the table's data, and "b" is taken from a [Parameter] to be filled in by the user.
 * This class is responsible for creating that [Parameter], and extracting the value to be passed to the
 * accept method.
 */
interface BType {

    val klass: Class<*>

    fun createParameter(): Parameter

    fun copyValue(fromParameter: Parameter, toParameter: Parameter)

    fun getValue(parameter: Parameter): Any?

}

abstract class AbstractBType : BType {

    override fun equals(obj: Any?): Boolean {
        return obj?.javaClass === this.javaClass
    }

    override fun hashCode(): Int {
        return 1 + javaClass.hashCode()
    }
}

abstract class ValueParameterBType<T>() : AbstractBType() {

    override fun copyValue(fromParameter: Parameter, toParameter: Parameter) {
        @Suppress("UNCHECKED_CAST")
        (toParameter as ValueParameter<T>).value = (fromParameter as ValueParameter<T>).value
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(parameter: Parameter): T = (parameter as ValueParameter<T>).value

}

class BooleanBType() : ValueParameterBType<Boolean?>() {

    override val klass = java.lang.Boolean::class.java

    override fun createParameter(): BooleanParameter {
        val parameter = BooleanParameter("booleanValue", label = "")
        parameter.asComboBox()
        return parameter
    }
}

class IntBType() : ValueParameterBType<Int?>() {

    override val klass = java.lang.Integer::class.java
    override fun createParameter() = IntParameter("intValue", label = "")
}

class DoubleBType() : ValueParameterBType<Double?>() {

    override val klass = java.lang.Double::class.java
    override fun createParameter() = DoubleParameter("doubleValue", label = "")
}

class StringBType() : ValueParameterBType<String>() {

    override val klass = java.lang.String::class.java
    override fun createParameter() = StringParameter("stringValue", label = "")
}

class LocalDateBType() : ValueParameterBType<LocalDate>() {
    override val klass = LocalDate::class.java
    override fun createParameter() = DateParameter("dateValue", label = "")
}

class TemporalAmountBType() : AbstractBType() {
    override val klass = TemporalAmount::class.java
    override fun createParameter() = TemporalAmountParameter("temporalAmountValue", label = "")

    override fun copyValue(fromParameter: Parameter, toParameter: Parameter) {
        if (toParameter is TemporalAmountParameter && fromParameter is TemporalAmountParameter) {
            toParameter.amount = fromParameter.amount
            toParameter.units = fromParameter.units
        }
    }

    override fun getValue(parameter: Parameter) = (parameter as TemporalAmountParameter).value
}

class IntRangeBType : AbstractBType() {

    override val klass = IntRangeParameter::class.java

    override fun createParameter(): IntRangeParameter = IntRangeParameter("intRangeValue", label = "")

    override fun copyValue(fromParameter: Parameter, toParameter: Parameter) {
        if (toParameter is IntRangeParameter && fromParameter is IntRangeParameter) {
            toParameter.from = fromParameter.from
            toParameter.to = fromParameter.to
        }
    }

    override fun getValue(parameter: Parameter) = parameter
}
