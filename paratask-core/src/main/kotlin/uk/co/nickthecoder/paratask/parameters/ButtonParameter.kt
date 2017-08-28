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

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.parameters.fields.ButtonField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

class ButtonParameter(
        name: String,
        label: String = name.uncamel(),
        val buttonText: String,
        description: String = "",
        val action: (ButtonField) -> Unit)
    : AbstractParameter(
        name = name,
        label = label,
        description = description) {

    override fun isStretchy(): Boolean = false

    override fun copy(): ButtonParameter = ButtonParameter(name, label, buttonText, description, action)

    override fun createField(): ButtonField = ButtonField(this).build() as ButtonField

    override fun errorMessage(): String? = null
}
