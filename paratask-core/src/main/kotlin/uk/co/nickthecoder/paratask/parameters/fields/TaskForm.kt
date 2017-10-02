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
import javafx.scene.control.ScrollPane
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.ValueParameter

class TaskForm(val task: Task) {

    val form = ParametersForm(task.taskD.root, null)

    val scrollPane = ScrollPane(form)

    fun build(): ScrollPane {
        scrollPane.fitToWidthProperty().set(true)
        form.buildContent()
        task.taskD.width?.let { scrollPane.prefWidth = it.toDouble() }
        task.taskD.height?.let { scrollPane.prefHeight = it.toDouble() }

        return scrollPane
    }

    fun check(): Boolean {

        val programming = task.taskD.programmingMode

        // Are there any "dirty" fields, where the value in the GUI isn't in the Value.
        // For example, if a non-valid number is typed into an IntField
        form.descendants().forEach { field ->
            val parameter = field.parameter
            if (!programming || (parameter is ValueParameter<*> && parameter.expression != null)) {
                field.makeClean()
                if (field.isDirty()) {
                    ensureVisible(field.control!!)
                    return false
                }
            }
        }

        form.descendants().forEach { field ->
            field.clearError()
        }

        try {
            task.check()

        } catch (e: ParameterException) {
            val field = form.findField(e.parameter)
            if (field != null) {
                field.showError(e.message)
                ensureVisible(field.control!!)
            } else {
                println("TaskForm.check : Couldn't show error message : '${e.message} for parameter ${e.parameter}.")
            }

            return false
        }
        return true
    }

    private fun ensureVisible(node: Node) {

        // Ensures the field is visible within the scrollPane
        val viewport = scrollPane.viewportBounds
        val contentHeight = scrollPane.content.boundsInLocal.height

        val bounds = scrollPane.content.sceneToLocal(node.localToScene(node.boundsInLocal))

        val nodeMinY = bounds.minY
        val nodeMaxY = bounds.maxY

        val viewportMinY = (contentHeight - viewport.height) * scrollPane.vvalue
        val viewportMaxY = viewportMinY + viewport.height
        if (nodeMinY < viewportMinY) {
            scrollPane.vvalue = nodeMinY / (contentHeight - viewport.height)
        } else if (nodeMaxY > viewportMaxY) {
            scrollPane.vvalue = (nodeMaxY - viewport.height) / (contentHeight - viewport.height)
        }
    }

}
