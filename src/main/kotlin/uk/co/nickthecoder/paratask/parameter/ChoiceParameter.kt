package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.ChoiceField
import uk.co.nickthecoder.paratask.util.uncamel

class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        value: T,
        required: Boolean = true)
    : ValueParameter<ChoiceValue<T?>>(name = name, label = label, required = required) {

    override fun errorMessage(values: Values): String? = errorMessage(parameterValue(values).value)

    val choiceValue = ChoiceValue<T>(this, value)

    fun choice(key: String, value: T?, label: String = key.uncamel()) {
        choiceValue.addChoice(key, value, label)
    }

    fun errorMessage(v: T?): String? {
        return if (v == null && required) "Required" else null
    }

    override fun isStretchy() = false

    override fun createField(values: Values): ChoiceField<T> = ChoiceField<T>(this, values)

    override fun createValue() = choiceValue.copy()

    fun parameterValue(values: Values): ChoiceValue<T> = values.get(name) as ChoiceValue<T>

    fun value(values: Values) = parameterValue(values).value

    override fun copyValue(source: Values): ChoiceValue<T> {
        val copy = ChoiceValue<T>(this, parameterValue(source).value)
        return copy
    }

    override fun toString(): String {
        return "ChoiceParameter ${name}"
    }

}
