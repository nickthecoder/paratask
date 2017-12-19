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
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

data class Choice<T>(val key: String, val value: T, val label: String)

open class ChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: T? = null,
        required: Boolean = true)

    : AbstractValueParameter<T?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    protected val choices = mutableListOf<Choice<T>>()

    override val converter = object : StringConverter<T?>() {

        override fun fromString(str: String): T? {
            return choices.firstOrNull { it.key == str }?.value
        }

        override fun toString(obj: T?): String {
            return choices.firstOrNull { it.value == obj }?.key ?: "<unknown>"
        }
    }

    fun choice(key: String, value: T, label: String = key.uncamel()): ChoiceParameter<T> {
        addChoice(key, value, label)
        return this
    }

    override fun isStretchy() = false


    override fun errorMessage(): String? {
        return super.errorMessage()
    }

    override fun errorMessage(v: T?): String? {
        if (isProgrammingMode()) return null

        if (v == null) return super.errorMessage(v)

        if (choices.firstOrNull { it.value == v } == null) {
            return "Invalid choice"
        }

        return null
    }

    override fun createField(): ParameterField {
        val result = ChoiceField(this)
        result.build()
        return result
    }

    fun choices(): List<Choice<T>> {
        return choices
    }

    fun getLabelForValue(value: T?): String? {
        return choices.firstOrNull { it.value == value }?.label
    }

    fun getValueForLabel(label: String?): T? {
        return choices.firstOrNull { it.label == label }?.value
    }

    open fun addChoice(key: String, value: T, label: String = key.uncamel()): ChoiceParameter<T> {
        choices.add(Choice(key, value, label))

        parameterListeners.fireStructureChanged(this)
        return this
    }

    fun removeKey(key: String) {
        choices.removeIf { it.key == key }

        parameterListeners.fireStructureChanged(this)
    }

    fun clear() {
        choices.clear()

        parameterListeners.fireStructureChanged(this)
    }

    override fun coerce(v: Any?) {
        // If it is in the map of values, then we can use it without problem
        if (choices.firstOrNull { it.value == v } != null) {
            @Suppress("UNCHECKED_CAST")
            value = v as T

        } else {
            val str = v.toString()
            choices.firstOrNull { str == it.value.toString() }?.let {
                value = it.value
                return
            }
            super.coerce(v)
        }
    }

    override fun copy(): ChoiceParameter<T> {
        val result = ChoiceParameter(name = name, label = label, description = description, value = value!!,
                required = required)

        result.choices.addAll(choices)
        return result
    }

    override fun toString(): String = "Choice" + super.toString()

}

inline fun <reified T : Enum<T>> ChoiceParameter<T?>.nullableEnumChoices(
        nullLabel: String = "",
        nullKey: String = "null",
        mixCase: Boolean = false): ChoiceParameter<T?> {

    choice(nullKey, null, nullLabel)

    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) {
            item.label
        } else if (mixCase) {
            item.name.split("_").map { it.toLowerCase().capitalize() }.joinToString(separator = " ")
        } else {
            item.name
        }
        choice(key = item.name, value = item, label = label)
    }
    return this
}

inline fun <reified T : Enum<T>> ChoiceParameter<T>.enumChoices(mixCase: Boolean = false): ChoiceParameter<T> {
    enumValues<T>().forEach { item ->
        val label = if (item is Labelled) {
            item.label
        } else {
            if (mixCase) {
                item.name.split("_").map { it.toLowerCase().capitalize() }.joinToString(separator = " ")
            } else {
                item.name
            }
        }
        choice(key = item.name, value = item, label = label)
    }
    return this
}
