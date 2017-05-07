package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import javafx.scene.control.TitledPane
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
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
        set(newValue) {
            innerParameters.clear()
            for (item in newValue) {
                val innerParameter = factory()
                innerParameters.add(innerParameter)
                innerParameter.value = item
            }
            parameterListeners.fireStructureChanged(this)
        }

    override val converter = object : StringConverter<List<T>>() {

        override fun fromString(str: String): List<T>? {
            if (str == "") {
                return listOf<T>()
            }
            val lines = str.split('\n')
            val result = lines.map {
                val innerParameter = factory()
                innerParameter.converter.fromString(it)
            }
            return result
        }

        override fun toString(obj: List<T>?): String {
            if (obj == null) {
                return ""
            }
            val strings = obj.map {
                val innerParameter = factory()
                innerParameter.converter.toString(it)
            }
            return strings.joinToString(separator = "\n")
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
        parameterListeners.fireStructureChanged(this)
    }

    val innerListener = object : ParameterListener {
        override fun parameterChanged(event: ParameterEvent) {
            parameterListeners.fireInnerParameterChanged(this@MultipleParameter, event.parameter)
        }
    }

    private fun createInnerParameter() {
        val innerParameter = factory()
        innerParameter.parameterListeners.add(innerListener)
    }

    fun newValue(index: Int = value.size) {
        val innerParameter = factory()

        innerParameters.add(index, innerParameter)
        parameterListeners.fireStructureChanged(this)
    }

    fun addValue(item: T, index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.value = item

        innerParameters.add(index, innerParameter)
        parameterListeners.fireStructureChanged(this)
    }

    fun removeAt(index: Int) {
        innerParameters.removeAt(index)
        parameterListeners.fireStructureChanged(this)
    }

    override fun toString(): String = "Multiple" + super.toString() + " = " + value
}

