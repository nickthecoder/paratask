package uk.co.nickthecoder.paratask.gui.project

import javafx.event.EventHandler
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

    private val applyButton = Button("Apply")

    private lateinit var toolPane: ToolPane

    private lateinit var focusListener: FocusListener

    init {
        center = taskForm.scrollPane
        bottom = buttons

        stopButton.onAction = EventHandler { onStop() }
        runButton.onAction = EventHandler { onRun() }
        applyButton.onAction = EventHandler { onApply() }

        val runStop = StackPane()
        runStop.children.addAll(stopButton, runButton)

        stopButton.visibleProperty().bind(tool.toolRunner.showStopProperty)
        runButton.visibleProperty().bind(tool.toolRunner.showRunProperty)
        applyButton.visibleProperty().bind(tool.toolRunner.showRunProperty)
        runButton.disableProperty().bind(tool.toolRunner.disableRunProperty)
        applyButton.disableProperty().bind(tool.toolRunner.disableRunProperty)

        buttons.children.addAll(applyButton, runStop)
        buttons.getStyleClass().add("buttons")
    }

    override fun run(): Boolean {
        val copiedValues = taskForm.check()
        if (copiedValues != null) {

            toolPane.halfTab.pushHistory(tool, copiedValues)

            tool.toolRunner.run(copiedValues)
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
        if (run()) {
            toolPane.showJustResults()
        }
    }

    private fun onApply() {
        run()
    }

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane

        // Note, we must get the scene in a weird way because node.getScene() is still null at this point.
        // SplitPane doesn't set the parent of its items straight away. SplitPane.getItems().add() is WEIRD!
        val scene = toolPane.halfTab.projectTab.projectTabs.getScene()
        focusListener = FocusListener(this, scene = scene) { hasFocus: Boolean ->
            runButton.setDefaultButton(hasFocus)
        }
    }

    override fun detaching() {
        focusListener.remove()
    }
}
