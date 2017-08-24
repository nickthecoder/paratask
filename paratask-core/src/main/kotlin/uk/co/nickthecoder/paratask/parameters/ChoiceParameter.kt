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

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.fields.ChoiceField
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel


open class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T,
        required: Boolean = true)

    : AbstractValueParameter<T?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {


    private val valueToKeyMap = LinkedHashMap<T?, String>()
    private val keyToValueMap = LinkedHashMap<String, T?>()
    private val valueToLabelMap = LinkedHashMap<T?, String>()
    private val labelToValueMap = LinkedHashMap<String, T?>()

    override val converter = object : StringConverter<T?>() {

        override fun fromString(str: String): T? {
            return keyToValueMap[str]
        }

        override fun toString(obj: T?): String {
            return valueToKeyMap[obj] ?: "<unknown>"
        }
    }

    fun choice(key: String, value: T?, label: String = key.uncamel()): ChoiceParameter<T> {
        addChoice(key, value, label)
        return this
    }

    override fun isStretchy() = false


    override fun errorMessage(v: T?): String? {
        if (isProgrammingMode()) return null

        if (v == null) return super.errorMessage(v)

        if (valueToKeyMap[v] == null) {
            return "Invalid choice"
        }

        return null
    }

    override fun createField(): ChoiceField<T> {
        val result = ChoiceField(this)
        result.build()
        return result
    }

    override fun toString(): String = "Choice" + super.toString()

    fun choiceValues(): Collection<T?> {
        return keyToValueMap.values
    }

    fun getLabelForValue(value: T?): String? {
        return valueToLabelMap[value]
    }

    fun getValueForLabel(label: String?): T? {
        return labelToValueMap[label]
    }

    fun choiceKeys(): Collection<String> = valueToKeyMap.values

    fun valueKey() = valueToKeyMap[value]

    fun addChoice(key: String, value: T?, label: String = key.uncamel()) {
        keyToValueMap.put(key, value)
        valueToKeyMap.put(value, key)
        valueToLabelMap.put(value, label)
        labelToValueMap.put(label, value)

        parameterListeners.fireStructureChanged(this)
    }

    fun removeKey(key: String) {
        val value = keyToValueMap[key]
        val label = valueToLabelMap[value]

        keyToValueMap.remove(key)
        valueToKeyMap.remove(value)
        valueToLabelMap.remove(value)
        labelToValueMap.remove(label)

        parameterListeners.fireStructureChanged(this)
    }

    fun clear() {
        keyToValueMap.clear()
        valueToLabelMap.clear()
        labelToValueMap.clear()

        parameterListeners.fireStructureChanged(this)
    }

    override fun coerce(v: Any?) {
        // If it is in the map of values, then we can use it without problem
        if (valueToKeyMap.containsKey(v)) {
            @Suppress("UNCHECKED_CAST")
            value = v as T

        } else {
            val str = v.toString()
            valueToKeyMap.keys.first { str == it.toString() }?.let {
                value = it
                return
            }
            super.coerce(v)
        }
    }

    override fun copy(): ChoiceParameter<T> {
        val result = ChoiceParameter(name = name, label = label, description = description, value = value!!,
                required = required)

        keyToValueMap.forEach { (key, value) ->
            result.addChoice(key, value, valueToLabelMap[value]!!)
        }
        return result
    }

}

inline fun <reified T : Enum<T>> ChoiceParameter<T?>.nullableEnumChoices(nullLabel: String = ""): ChoiceParameter<T?> {
    choice("", null, nullLabel)
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) item.label else item.name
        choice(key = item.name, value = item, label = label)
    }
    return this
}

inline fun <reified T : Enum<T>> ChoiceParameter<T>.enumChoices(): ChoiceParameter<T> {
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) item.label else item.name
        choice(key = item.name, value = item, label = label)
    }
    return this
}
