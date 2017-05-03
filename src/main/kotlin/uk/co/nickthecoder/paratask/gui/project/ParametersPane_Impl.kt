package uk.co.nickthecoder.paratask.gui.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.gui.field.TaskForm
import uk.co.nickthecoder.paratask.project.Tool

class ParametersPane_Impl(override val tool: Tool)

    : ParametersPane, BorderPane() {

    override val taskForm = TaskForm(tool)

    private val buttons = ButtonBar()

    private val runButton = Button("Run")

    private val applyButton = Button("Apply")

    private lateinit var toolPane: ToolPane

    private lateinit var focusListener: FocusListener

    init {
        center = taskForm.scrollPane
        bottom = buttons

        ButtonBar.setButtonData(runButton, ButtonData.OK_DONE)
        ButtonBar.setButtonData(applyButton, ButtonData.APPLY)

        runButton.onAction = EventHandler { onRun() }
        applyButton.onAction = EventHandler { onApply() }

        buttons.getButtons().addAll(runButton, applyButton)
        buttons.getStyleClass().add("buttons")
    }

    override fun run(): Boolean {
        val copiedValues = taskForm.check()
        if (copiedValues != null) {
            tool.toolRunner.run(copiedValues)
            return true
        }
        return false
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
