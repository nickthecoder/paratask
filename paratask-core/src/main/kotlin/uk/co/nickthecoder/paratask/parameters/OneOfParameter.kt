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

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.OneOfField
import uk.co.nickthecoder.paratask.util.uncamel

class OneOfParameter(
        name: String,
        val required: Boolean = true,
        label: String = name.uncamel(),
        description: String = "",
        val message: String = "Choose",
        value: Parameter? = null)

    : AbstractGroupParameter(name, label = label, description = description), PropertyValueParameter<Parameter?> {

    override val expressionProperty = SimpleStringProperty()

    override val converter: StringConverter<Parameter?> = object : StringConverter<Parameter?>() {
        override fun toString(v: Parameter?): String = v?.name ?: ""

        override fun fromString(v: String): Parameter? {
            if (v == "") return null
            return children.firstOrNull { it.name == v }
        }
    }

    override val valueProperty = SimpleObjectProperty<Parameter?>()

    init {
        this.value = value
    }


    override fun createField(): OneOfField {
        val result = OneOfField(this)
        result.build()
        return result
    }

    override fun errorMessage(): String? {
        return errorMessage(value)
    }

    override fun errorMessage(v: Parameter?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            if (required) {
                return "You must choose an item from the list"
            }
            return null
        } else {
            return v.errorMessage()
        }
    }

    override fun check() {
        errorMessage()?.let { throw ParameterException(this, it) }

        value?.let { checkChild(it) }
    }


    override fun copy(): OneOfParameter {
        val result = OneOfParameter(name = name, label = label, description = description, value = null, message = message)
        children.forEach { child ->
            val copiedChild = child.copy()
            result.addParameters(copiedChild)
            if (child === value) {
                result.value = copiedChild
            }
        }
        return result
    }

}
