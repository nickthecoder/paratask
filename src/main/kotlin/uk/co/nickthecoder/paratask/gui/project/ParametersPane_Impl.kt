package uk.co.nickthecoder.paratask.gui.project

import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.gui.field.TaskForm
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.Tool

class ParametersPane_Impl(override val tool: Tool)

    : ParametersPane, BorderPane() {

    override val taskForm = TaskForm(tool)

    private val buttons = FlowPane()

    private val runButton = Button("Run")

    private val stopButton = Button("Stop")

    private lateinit var toolPane: ToolPane

    private lateinit var focusListener: FocusListener

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
        buttons.getStyleClass().add("buttons")
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

        focusListener = FocusListener(this) { hasFocus: Boolean ->
            runButton.setDefaultButton(hasFocus)
        }
    }

    override fun detaching() {
        focusListener.remove()
    }

}
