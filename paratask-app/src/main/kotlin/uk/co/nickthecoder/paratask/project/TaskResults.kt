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
package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.AbstractTaskPrompter
import uk.co.nickthecoder.paratask.util.focusNext

class TaskResults(tool: Tool, val task: Task) : AbstractResults(tool, "Meta Data") {

    val prompter = ResultsTaskPrompter()

    override val node = prompter.root

    override fun focus() {
        node.focusNext()
    }

    init {
        prompter.build()
    }

    inner class ResultsTaskPrompter : AbstractTaskPrompter(task) {

        override fun build() {
            super.build()

            with(okButton) {
                visibleProperty().bind(task.taskRunner.showRunProperty)
                disableProperty().bind(task.taskRunner.disableRunProperty)
                defaultButtonProperty().set(true)
                onAction = javafx.event.EventHandler {
                    onOk()
                }
            }
            buttons.children.addAll(okButton)
        }

        val okButton = Button("OK")

        fun onOk() {
            task.taskRunner.run()
        }
    }
}