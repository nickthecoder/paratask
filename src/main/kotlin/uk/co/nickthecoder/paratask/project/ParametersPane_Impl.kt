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
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.FocusHelper
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.Stoppable

class ParametersPane_Impl(override val tool: Tool)

    : ParametersPane, BorderPane(), FocusListener {

    override val taskForm = TaskForm(tool)

    private val buttons = FlowPane()

    private val runButton = Button("Run")

    private val stopButton = Button("Stop")

    private lateinit var toolPane: ToolPane

    private lateinit var focusHelper: FocusHelper

    init {
        center = taskForm.scrollPane
        bottom = buttons

        stopButton.onAction = EventHandler { onStop() }
        runButton.onAction = EventHandler { onRun() }

        val runStop = StackPane()
        runStop.children.addAll(stopButton, runButton)

        stopButton.visibleProperty().bind(tool.taskRunner.showStopProperty)
        runButton.visibleProperty().bind(tool.taskRunner.showRunProperty)
        runButton.disableProperty().bind(tool.taskRunner.disableRunProperty)

        buttons.children.addAll(runStop)
        buttons.styleClass.add("buttons")
    }

    override fun run(): Boolean {

        if (taskForm.check()) {

            toolPane.halfTab.pushHistory(tool)

            tool.taskRunner.run()

            return true
        }
        return false
    }

    private fun onStop() {
        if (tool is Stoppable) {
            tool.stop()
        }
    }

    private fun onRun() {
        run()
    }

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
        focusHelper = FocusHelper(this, this, name = "ParametersPane")
    }

    override fun focusChanged(gained: Boolean) {
        runButton.setDefaultButton(gained)
    }

    override fun detaching() {
        focusHelper.remove()
    }

}
