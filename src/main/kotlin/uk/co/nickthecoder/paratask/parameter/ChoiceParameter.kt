package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.ChoiceField
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.paratask.util.Labelled

open class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T,
        required: Boolean = true)

    : ValueParameter<T>(
        name = name,
        label = label,
        description = description,
        value = value,
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

    override fun getParameterValue(values: Values) = super.getParameterValue(values) as ChoiceValue<T>

    override fun toString(): String = "Choice" + super.toString()

}

inline fun <reified T : Enum<T>> ChoiceParameter<T>.enumChoices(): ChoiceParameter<T> {
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) item.label else item.name
        this.choice(key = item.name, value = item, label = label)
    }
    return this
}
