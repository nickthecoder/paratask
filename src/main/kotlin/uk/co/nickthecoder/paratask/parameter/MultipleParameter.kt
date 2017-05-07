package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.field.MultipleField
import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.gui.field.WrappableField
import uk.co.nickthecoder.paratask.util.uncamel

class MultipleParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val allowInsert: Boolean = false,
        val minItems: Int = 0,
        val maxItems: Int = Int.MAX_VALUE,
        val factory: () -> ValueParameter<T>)

    : ValueParameter<List<T>>, AbstractParameter(
        name = name,
        label = label,
        description = description),
        WrappableField {

    internal val innerParameters = mutableListOf<ValueParameter<T>>()

    override var value: List<T>
        get() = innerParameters.map { it.value }
        set(value) {
            innerParameters.clear()
            for (item in value) {
                val innerParameter = factory()
                innerParameters.add(innerParameter)
            }
            parameterListeners.fireChanged(this)

        }

    // TODO Prevent the list from being altered from outside.
    // Declare as a List, and have a private reference to the MutableList?
    override val converter = object : StringConverter<List<T>>() {

        override fun fromString(str: String): List<T>? {
            // TODO Create from string
            return mutableListOf<T>()
        }

        override fun toString(obj: List<T>?): String {
            return ""
            // TODO Create string
        }
    }

    override fun isStretchy() = true

    override fun errorMessage(v: List<T>?): String? {

        if (v == null) {
            return "Expected a list of items"
        }

        if (v.size < minItems) {
            return "Must have at least ${minItems} items"
        }
        if (v.size > maxItems) {
            return "Cannot have more than ${maxItems} items"
        }

        var index = 0
        for (innerParameter in innerParameters) {

            innerParameter.errorMessage()?.let { return "Item #${index + 1} : ${it}" }
            index++
        }

        return null
    }

    override fun createField() = MultipleField(this)

    override fun wrap(parameterField: ParameterField): Node {
        val titledPane = TitledPane(label, parameterField)
        titledPane.setCollapsible(false)
        return titledPane
    }


    fun clear() {
        innerParameters.clear()
        parameterListeners.fireChanged(this)
    }

    fun newValue(index: Int = value.size) {
        val innerParameter = factory()

        innerParameters.add(index, innerParameter)
        parameterListeners.fireChanged(this)
    }

    fun addValue(item: T, index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.value = item

        innerParameters.add(index, innerParameter)
        parameterListeners.fireChanged(this)

    }

    fun removeAt(index: Int) {
        innerParameters.removeAt(index)
        parameterListeners.fireChanged(this)
    }

}

