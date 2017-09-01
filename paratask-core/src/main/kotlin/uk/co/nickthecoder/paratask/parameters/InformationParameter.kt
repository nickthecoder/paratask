/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
import uk.co.nickthecoder.paratask.parameters.fields.InformationField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * Allows any text to appear on a form.
 */
class InformationParameter(
        name: String,
        label: String = "",
        val information: String,
        style: String? = null)

    : AbstractParameter(name, label = label, description = "") {

    var style: String? = style
        set(v) {
            styleProperty.set(v)
        }

    val styleProperty = SimpleStringProperty()

    override fun errorMessage(): String? = null

    override fun isStretchy(): Boolean = true

    override fun createField() = InformationField(this).build() as InformationField

    override fun copy(): InformationParameter {
        return InformationParameter(name = name, information = information, label = label, style = style)
    }
}
