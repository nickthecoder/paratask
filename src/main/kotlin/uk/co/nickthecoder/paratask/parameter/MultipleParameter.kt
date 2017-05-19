package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.field.MultipleField
import uk.co.nickthecoder.paratask.util.uncamel

class MultipleParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val allowInsert: Boolean = false,
        val minItems: Int = 0,
        val maxItems: Int = Int.MAX_VALUE,
        value: List<T>? = null,
        val factory: () -> ValueParameter<T>)

    : AbstractParameter(
        name = name,
        label = label,
        description = description), ValueParameter<List<T>>, ParentParameter {

    internal val innerParameters = mutableListOf<ValueParameter<T>>()

    override val children: List<Parameter> = innerParameters

    override val expressionProperty = SimpleStringProperty()

    override var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    override var value: List<T>
        get() = innerParameters.map { it.value }
        set(newValue) {
            innerParameters.clear()
            for (item in newValue) {
                val innerParameter = factory()
                innerParameter.parent = this
                innerParameters.add(innerParameter)
                innerParameter.value = item
            }
            expression = null
            parameterListeners.fireStructureChanged(this@MultipleParameter)
        }

    init {
        value?.let { this.value = value }
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

        /*
        var index = 0
        for (innerParameter in innerParameters) {

            innerParameter.errorMessage()?.let { return "Item #${index + 1} : ${it}" }
            index++
        }
        */

        return null
    }

    override fun createField(): MultipleField<T> {
        val result = MultipleField(this)
        result.buildContent()
        return result
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

    fun newValue(index: Int = value.size) : ValueParameter<T> {
        val innerParameter = factory()
        innerParameter.parent = this

        innerParameters.add(index, innerParameter)
        parameterListeners.fireStructureChanged(this)
        return innerParameter
    }

    fun addValue(item: T, index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.parent = this

        innerParameter.value = item

        innerParameters.add(index, innerParameter)
        parameterListeners.fireStructureChanged(this)
    }

    fun addStringValue(str: String, index: Int = value.size) {
        val innerParameter = factory()
        innerParameter.parent = this

        innerParameter.stringValue = str

        innerParameters.add(index, innerParameter)
        parameterListeners.fireStructureChanged(this)
    }

    fun removeAt(index: Int) {
        innerParameters.removeAt(index)
        parameterListeners.fireStructureChanged(this)
    }

    override fun toString(): String = "Multiple" + super.toString() + " = " + value
}

