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

package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

class EditTaskPrompter(task: Task, val scriptVariables: ScriptVariables?, val onDone: (() -> Unit)? = null)
    : AbstractTaskPrompter(task) {

    val variablesButton = Button("Variables")

    val doneButton = Button("Done")

    override fun build() {

        super.build()

        with(doneButton) {
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
            onAction = EventHandler {
                onDone()
            }
        }

        if (scriptVariables != null && scriptVariables.map.isNotEmpty()) {
            variablesButton.onAction = EventHandler {
                val prompter = VariablePrompter(scriptVariables)
                prompter.build()
                prompter.show()
            }
            buttons.children.add(variablesButton)
        }

        buttons.children.add(doneButton)

    }

    fun onDone() {
        close()
        onDone?.let { it() }
    }

}
