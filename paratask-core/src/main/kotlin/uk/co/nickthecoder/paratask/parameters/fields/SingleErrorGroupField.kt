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
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.ParameterListener


abstract class SingleErrorGroupField(
        val groupParameter: GroupParameter,
        isBoxed: Boolean)

    : ParameterField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val vBox = VBox()

    /**
     * A single error label beneath the fields. If more than one field is in error, only one error is displayed.
     * Named error2 to avoid clashing with the error for THIS ParameterField (which will never be used, as
     * GroupParameters are never in error).
     */
    var error2: Label? = null

    override fun createControl(): Node {
        vBox.children.add(createContent())
        vBox.styleClass.add("horizontal-container")
        return vBox
    }

    abstract fun createContent(): Node

    override fun updateError(field: ParameterField) {
        if ((!field.parameter.hidden) && field.error.isVisible && field.error !== error2) {
            error2 = field.error
            vBox.children.add(error2)
        } else if (error2 === field.error) {
            vBox.children.remove(error2)
            error2 = null
        }
    }
    
}
