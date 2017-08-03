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
import javafx.scene.layout.Region
import uk.co.nickthecoder.paratask.parameters.Parameter

open class ParameterField(open val parameter: Parameter) : Region() {

    lateinit var form: FieldParent

    val error = Label()

    init {
        error.isVisible = false
        error.styleClass.add("error")

        children.add(error)
    }

    open var control: Node? = null
        set(v) {
            if (field != null) {
                children.remove(field)
            }
            field = v
            if (v != null) {
                v.styleClass.add("control")
                children.add(v)
            }
        }

    fun showError(message: String) {
        error.text = message
        error.visibleProperty().value = true
        // Without this, the layout sometimes screwed up. I found this form the GoKo project,
        // Its TimeLimits preferences did't layout correctly when OK was pressed from a *different* preferences tab.
        requestLayout()
    }

    fun clearError() {
        error.visibleProperty().value = false
    }

    open fun isDirty(): Boolean = false

}
