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

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.defaultWhileFocusWithin
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.focusNext


open class ParametersPane_Impl(final override val task: Task)

    : ParametersPane, BorderPane() {

    final override val taskForm = TaskForm(task)

    protected val buttons = FlowPane()

    val runButton = Button("Run")

    protected val stopButton = Button("Stop")

    protected lateinit var toolPane: ToolPane

    init {
        taskForm.build()

        center = taskForm.scrollPane
        bottom = buttons

        stopButton.onAction = EventHandler { onStop() }
        runButton.onAction = EventHandler { onRun() }

        val runStop = StackPane()
        runStop.children.addAll(stopButton, runButton)

        stopButton.visibleProperty().bind(task.taskRunner.showStopProperty)
        runButton.visibleProperty().bind(task.taskRunner.showRunProperty)
        runButton.disableProperty().bind(task.taskRunner.disableRunProperty)

        buttons.children.addAll(runStop)
        buttons.styleClass.add("buttons")
    }

    override fun run(): Boolean {

        if (!task.taskRunner.isRunning()) {
            task.resolveParameters(toolPane.halfTab.projectTab.projectTabs.projectWindow.project.resolver)

            if (taskForm.check()) {

                task.taskRunner.run()
                return true
            }
        }
        return false
    }

    private fun onStop() {
        if (task is Stoppable) {
            (task as Stoppable).stop()
        }
    }

    private fun onRun() {
        run()
    }

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
        runButton.defaultWhileFocusWithin(this, "ParametersPane Run")
    }

    override fun detaching() {
    }

    override fun focus() {
        ParaTaskApp.logFocus("ParametersPane_Impl focus. focusNext()")
        this.focusNext()
    }

}
