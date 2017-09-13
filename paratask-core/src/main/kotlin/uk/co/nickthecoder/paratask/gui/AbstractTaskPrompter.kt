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

import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.findScrollbar

abstract class AbstractTaskPrompter(val task: Task) {

    var root = BorderPane()

    var taskForm = TaskForm(task)

    var stage: Stage? = null

    val buttons = FlowPane()


    open protected fun close() {
        stage?.hide()
    }

    open fun build() {

        taskForm.build()
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

        ParaTask.style(scene)

        stage.scene = scene

        // Once the stage has been fully created, listen for when the taskForm's scroll bar appears, and
        // attempt to resize the stage, so that the scroll bar is no longer needed.
        Platform.runLater {
            val scrollBar = taskForm.scrollPane.findScrollbar(Orientation.VERTICAL)
            scrollBar?.visibleProperty()?.addListener { _, _, newValue ->
                if (newValue == true) {
                    val extra = taskForm.scrollPane.prefHeight(-1.0) - taskForm.scrollPane.height
                    if (extra > 0 && stage.height < 700) {
                        stage.sizeToScene()
                        Platform.runLater {
                            taskForm.form.requestLayout()
                        }
                    }
                }
            }
        }
        AutoExit.show(stage)
    }

}
