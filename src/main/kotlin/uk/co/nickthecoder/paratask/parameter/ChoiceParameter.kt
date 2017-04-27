package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.ChoiceField
import uk.co.nickthecoder.paratask.util.uncamel

class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T? = null,
        required: Boolean = true)

    : ValueParameter<T>(
        name = name,
        label = label,
        description = description,
        required = required) {


    val choiceValue = ChoiceValue<T>(this, value)

    fun choice(key: String, value: T?, label: String = key.uncamel()): ChoiceParameter<T> {
        choiceValue.addChoice(key, value, label)
        return this
    }

    override fun isStretchy() = false

    override fun createField(values: Values): ChoiceField<T> = ChoiceField<T>(this, values)

    override fun createValue() = choiceValue.copy()

    override fun copyValue(source: Values): ChoiceValue<T> {
        val copy = ChoiceValue<T>(this, value(source))
        return copy
    }

    override fun getValue(values: Values) = super.getValue(values) as ChoiceValue<T>

    override fun toString(): String = "Choice" + super.toString()

}
