package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter

/**
 * Base implementation of ValueParameter.
 */
abstract class AbstractValueParameter<T>(
        name: String,
        label: String,
        description: String,
        value: T,
        var required: Boolean = false)

    : AbstractParameter(name, label = label, description = description), PropertyValueParameter<T> {

    override abstract val converter: StringConverter<T>

    override var property = object : SimpleObjectProperty<T>() {
        override fun set(v: T) {
            val changed = v != get()
            if (changed) {
                super.set(v)
                parameterListeners.fireValueChanged(this@AbstractValueParameter)
            }
        }
    }

    override val expressionProperty = SimpleStringProperty()

    override var value: T
        set(v: T) {
            property.set(v)
            expression = null
        }
        get() = property.get()

    init {
        this.value = value
    }

    override open fun errorMessage(v: T?): String? = if (v == null && required) "Required" else null

    override fun toString() = super.toString() + " = " + value
}
