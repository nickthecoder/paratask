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

    : ValueParameter<MutableList<T>>(
        name = name,
        label = label,
        description = description,
        value = mutableListOf<T>(),
        required = true),
        WrappableField {

    internal val innerParameters = mutableListOf<ValueParameter<T>>()

    // TODO Prevent the list from being altered from outside.
    // Declare as a List, and have a private reference to the MutableList?
    override val converter = object : StringConverter<MutableList<T>>() {

        override fun fromString(str: String): MutableList<T>? {
            // TODO Create from string
            return mutableListOf<T>()
        }

        override fun toString(obj: MutableList<T>?): String {
            return ""
            // TODO Create string
        }
    }

    override fun isStretchy() = true

    override fun errorMessage(v: MutableList<T>?): String? {

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
        value.clear()
        parameterListeners.fireChanged(this)
    }

    val innerListener = object : ParameterListener {
        override fun parameterChanged(parameter: Parameter) {
            for (i in innerParameters.indices) {
                if (innerParameters[i] === parameter) {
                    value[i] = innerParameters[i].value
                    break
                }
            }
        }
    }

    fun newValue(index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.parameterListeners.add(innerListener)

        innerParameters.add(index, innerParameter)
        value.add(innerParameter.value)
        parameterListeners.fireChanged(this)
    }

    fun addValue(item: T, index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.parameterListeners.add(innerListener)
        innerParameter.value = item

        innerParameters.add(index, innerParameter)
        value[index] = item
        parameterListeners.fireChanged(this)

    }

    fun removeAt(index: Int) {
        value.removeAt(index)
        innerParameters.removeAt(index)
        parameterListeners.fireChanged(this)
    }

}
