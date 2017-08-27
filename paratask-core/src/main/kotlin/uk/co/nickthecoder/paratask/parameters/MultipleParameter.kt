/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.MultipleField
import uk.co.nickthecoder.paratask.util.escapeNL
import uk.co.nickthecoder.paratask.util.unescapeNL
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

    val innerParameters = mutableListOf<ValueParameter<T>>()

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

    override val converter = object : StringConverter<List<T>>() {

        override fun fromString(str: String): List<T>? {
            if (str == "") {
                return listOf()
            }
            val lines = (if (str.endsWith('\n')) str.substring(0, str.length - 1) else str).split('\n')
            val result = lines.map {
                val innerParameter = factory()
                innerParameter.converter.fromString(it.unescapeNL())
            }
            return result
        }

        override fun toString(obj: List<T>?): String {
            if (obj == null || obj.isEmpty()) {
                return ""
            }
            val strings = obj.map {
                val innerParameter = factory()
                innerParameter.converter.toString(it).escapeNL()
            }
            return strings.joinToString(separator = "\n", postfix = "\n")
        }
    }

    init {
        value?.let { this.value = value }
    }

    override fun isStretchy() = true

    fun evaluateMultiple(child: ValueParameter<*>, value: Iterable<*>) {
        var index = 0
        for (myChild in innerParameters) {
            if (myChild === child) {
                removeAt(index)
                value.forEach {
                    newValue(index).evaluated(it)
                    index++
                }
                return
            }
            index++
        }
        throw RuntimeException("Tried to evaluate innerParameter ${child}, but is not one of my children")
    }

    override fun check() {
        if (expression != null) {
            return
        }
        errorMessage()?.let { throw ParameterException(this, it) }
        super.check()
    }

    override fun errorMessage(v: List<T>?): String? {
        if (isProgrammingMode()) return null

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
        result.build()
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

    fun remove(value: T) {
        val found = innerParameters.filter { it.value == value }.firstOrNull()
        found?.let {
            innerParameters.remove(it)
            parameterListeners.fireStructureChanged(this)
        }
    }

    fun replace(value: T, newValue: T) {
        val found = innerParameters.filter { it.value == value }.firstOrNull()
        found?.let {
            found.value = newValue
        }
    }

    override fun coerce(v: Any?) {
        if (v is List<*>) {
            value = listOf()
            v.forEach { innerV ->
                val inner = newValue()
                inner.coerce(innerV)
            }
            return
        }
        super.coerce(v)
    }

    override fun toString(): String = "Multiple" + super.toString() + " = " + value

    override fun copy(): MultipleParameter<T> {
        val result = MultipleParameter<T>(name = name, label = label, description = description, value = value,
                minItems = minItems, maxItems = maxItems, factory = factory)

        value.forEach {
            result.addValue(it)
        }

        return result
    }
}

