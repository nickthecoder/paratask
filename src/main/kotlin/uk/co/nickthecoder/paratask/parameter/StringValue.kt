package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty

class StringValue(override val parameter: StringParameter, initialValue: String = "") : Value<String> {

    override val valueListeners = ValueListeners()

    var property = object : SimpleObjectProperty<String>("") {
        override fun set(v: String) {
            val changed = v != this.get()
            if (changed) {
                valueListeners.fireChanged(this@StringValue)
                super.set(v)
            }
        }
    }

    override var value: String
        set(v: String) {
            property.set(v)
        }
        get() = property.get()

    init {
        value = initialValue
    }

    override var stringValue: String
        get() = value
        set(v: String) {
            value = v
        }

    override fun errorMessage() = errorMessage(value)

    fun errorMessage(v: String) = parameter.errorMessage(v)

    override fun toString(): String = "StringValue name '${parameter.name}' = '${value}'"

}