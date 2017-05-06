package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter

/**
 * The base class for all Parameters, which can hold a value.
 */
abstract class ValueParameter<T>(
        name: String,
        label: String,
        description: String,
        value: T,
        var required: Boolean = false)

    : AbstractParameter(name, label = label, description = description) {

    abstract val converter: StringConverter<T>

    var property = object : SimpleObjectProperty<T>() {
        override fun set(v: T) {
            val changed = v != get()
            if (changed) {
                parameterListeners.fireChanged(this@ValueParameter)
                super.set(v)
            }
        }
    }

    var value: T
        set(v: T) {
            property.set(v)
        }
        get() = property.get()

    var stringValue: String
        get() = converter.toString(value)
        set(v: String) {
            value = converter.fromString(v)
        }

    init {
        this.value = value
    }

    override fun errorMessage(): String? = errorMessage(value)

    open fun errorMessage(v: T?): String? = if (v == null && required) "Required" else null

    fun multiple(
            allowInsert: Boolean = false,
            minItems: Int = 0,
            maxItems: Int = Int.MAX_VALUE): MultipleParameter<T> {

        val list = mutableListOf<T>()

        if (minItems > 0) {
            list.add(value)
        }
        return MultipleParameter(
                this, name = name,
                label = label,
                description = description,
                value = list,
                allowInsert = allowInsert,
                minItems = minItems,
                maxItems = maxItems)
    }

}
