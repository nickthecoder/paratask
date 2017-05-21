package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.MultipleField
import uk.co.nickthecoder.paratask.util.uncamel

class MultipleParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
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

    val innerListener = object : ParameterListener {
        override fun parameterChanged(event: ParameterEvent) {
            parameterListeners.fireInnerParameterChanged(this@MultipleParameter, event.parameter)
        }
    }

    init {
        value?.let { this.value = value }
    }

    override val converter = object : StringConverter<List<T>>() {

        override fun fromString(str: String): List<T>? {
            if (str == "") {
                return listOf()
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

    fun evaluateMultiple( child: ValueParameter<*>, value : Iterable<*>) {
        var index = 0
        for ( myChild in innerParameters ) {
            if (myChild === child ) {
                removeAt(index)
                value.forEach {
                    newValue(index).evaluated(it)
                    index ++
                }
                return
            }
            index ++
        }
        throw RuntimeException( "Tried to evaluate innerParameter ${child}, but is not one of my children")
    }

    override fun check() {
        if ( expression != null ) {
            return
        }
        errorMessage()?.let { throw ParameterException(this, it) }
        super.check()
    }

    override fun errorMessage(v: List<T>?): String? {

        if (v == null) {
            return "Expected a list of items"
        }

        if (v.size < minItems) {
            return "Must have at least $minItems items"
        }
        if (v.size > maxItems) {
            return "Cannot have more than $maxItems items"
        }

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

    private fun addInnerParameter(index: Int, initialise: (ValueParameter<T>) -> Unit): ValueParameter<T> {
        val innerParameter = factory()
        innerParameter.parent = this
        innerParameter.parameterListeners.add(innerListener)
        innerParameters.add(index, innerParameter)

        initialise(innerParameter)

        parameterListeners.fireStructureChanged(this)
        return innerParameter
    }

    fun newValue(index: Int = value.size): ValueParameter<T> {
        return addInnerParameter(index) {}
    }

    fun addValue(item: T, index: Int = value.size): ValueParameter<T> {
        return addInnerParameter(index) { it.value = item }
    }

    fun addStringValue(str: String, index: Int = value.size): ValueParameter<T> {
        return addInnerParameter(index) { it.stringValue = str }
    }

    fun removeAt(index: Int) {
        innerParameters.removeAt(index)
        parameterListeners.fireStructureChanged(this)
    }

    override fun toString(): String = "Multiple" + super.toString() + " = " + value
}

