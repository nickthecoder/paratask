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

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.AutoExit

abstract class AbstractTaskPrompter(val task: Task) {

    var root = BorderPane()

    var taskForm = TaskForm(task)

    var stage: Stage? = null

    val buttons = FlowPane()


    open protected fun close() {
        stage?.hide()
    }

    open protected fun build() {

        task.taskRunner.processors.add(CommandInTerminalWindow(task.taskD.label))

        with(buttons) {
            styleClass.add("buttons")
        }

        with(root) {
            styleClass.add("task-prompter")
            center = taskForm.scrollPane
            bottom = buttons
        }
    }

    fun placeOnStage(stage: Stage) {

        build()

        this.stage = stage
        stage.title = task.taskD.label

        val scene = Scene(root)

        ParaTaskApp.style(scene)

        stage.scene = scene
        AutoExit.show(stage)
    }

}
