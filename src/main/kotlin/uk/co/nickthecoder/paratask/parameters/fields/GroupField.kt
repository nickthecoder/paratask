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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.GroupParameter

/**
 * This is the Field created by GroupParameter and is also use in TaskForm to prompt a whole Task.
 */
open class GroupField(val groupParameter: GroupParameter)
    : ParameterField(groupParameter), WrappableField
{
    val parametersForm = ParametersForm( groupParameter )

    init {
        this.control = parametersForm
    }

    fun buildContent() {
        parametersForm.buildContent()
    }

    override fun wrap(): Node {
        return WrappedField(this)
    }
}
