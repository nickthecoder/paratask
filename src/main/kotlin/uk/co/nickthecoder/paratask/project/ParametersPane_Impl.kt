package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.gui.FocusListener

class ParametersPane_Impl(override val tool: uk.co.nickthecoder.paratask.Tool)

    : ParametersPane, javafx.scene.layout.BorderPane() {

    override val taskForm = uk.co.nickthecoder.paratask.parameters.fields.TaskForm(tool)

    private val buttons = javafx.scene.layout.FlowPane()

    private val runButton = javafx.scene.control.Button("Run")

    private val stopButton = javafx.scene.control.Button("Stop")

    private lateinit var toolPane: ToolPane

    private lateinit var focusListener: FocusListener

    init {
        center = taskForm.scrollPane
        bottom = buttons

        stopButton.onAction = javafx.event.EventHandler { onStop() }
        runButton.onAction = javafx.event.EventHandler { onRun() }

        val runStop = javafx.scene.layout.StackPane()
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
        if (tool is uk.co.nickthecoder.paratask.util.Stoppable) {
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
