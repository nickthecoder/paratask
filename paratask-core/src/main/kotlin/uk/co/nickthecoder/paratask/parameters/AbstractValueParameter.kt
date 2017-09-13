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

/**
 * Base implementation of ValueParameter.
 */
abstract class AbstractValueParameter<T>(
        name: String,
        label: String,
        description: String,
        value: T,
        var required: Boolean = false,
        isBoxed: Boolean = false)

    : AbstractParameter(
        name, label = label,
        description = description,
        isBoxed = isBoxed),

        PropertyValueParameter<T> {

    override abstract val converter: StringConverter<T>

    override var valueProperty = object : SimpleObjectProperty<T>() {
        override fun set(v: T) {
            val changed = v != get()
            if (changed) {
                super.set(v)
                parameterListeners.fireValueChanged(this@AbstractValueParameter)
            }
        }
    }

    override val expressionProperty = SimpleStringProperty()

    final override var value: T
        set(v) {
            valueProperty.set(v)
            expression = null
        }
        get() = valueProperty.get()

    init {
        this.value = value
    }

    override fun errorMessage(v: T?): String? = if (v == null && required && !isProgrammingMode()) "Required" else null

    override fun toString() = "${super.toString()} value='$value'"
}
