package uk.co.nickthecoder.paratask.gui.project

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.gui.field.TaskForm
import uk.co.nickthecoder.paratask.project.Tool

class ParametersPane_Impl(override val tool: Tool)

    : ParametersPane, BorderPane() {

    private val taskForm = TaskForm(tool.task)

    private val buttons = FlowPane()

    private val runButton = Button("Run")

    private lateinit var toolPane: ToolPane

    private lateinit var focusListener: FocusListener

    init {
        center = taskForm.scrollPane
        bottom = buttons

        with(buttons) {
            children.add(runButton)
            getStyleClass().add("buttons")
        }

        runButton.onAction = EventHandler {
            onRun()
        }
    }

    private fun onRun() {
        val copiedValues = taskForm.check()
        if (copiedValues != null) {
            tool.toolRunner.run(copiedValues)
        }
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
