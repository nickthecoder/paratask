package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.AutoExit

open class TaskPrompter(val task: Task, val values: Values) {

    var root: BorderPane

    var form: ParametersForm

    val scrollPane: ScrollPane

    var stage: Stage? = null

    val okButton: Button

    val cancelButton: Button

    val applyButton: Button

    init {
        root = BorderPane()

        okButton = Button("OK")
        okButton.onAction = EventHandler {
            onOk()
        }
        okButton.defaultButtonProperty().set(true)

        cancelButton = Button("Cancel")
        cancelButton.onAction = EventHandler { onCancel() }
        cancelButton.visibleProperty().set(false)
        cancelButton.cancelButtonProperty().set(true)

        applyButton = Button("Apply")
        applyButton.onAction = EventHandler { onApply() }
        //applyButton.visibleProperty().set(false)

        val buttons = FlowPane()
        buttons.styleClass.add("buttons")
        buttons.children.add(okButton)
        buttons.children.add(cancelButton)
        buttons.children.add(applyButton)

        form = GroupParametersForm(task.taskD.root, values)

        root.getStyleClass().add("task-prompter")

        scrollPane = ScrollPane(form)
        scrollPane.fitToWidthProperty().set(true)

        root.center = scrollPane
        root.bottom = buttons
    }

    private fun ensureVisible(pane: ScrollPane, node: Node) {
        // Cut and pasted code from stack overflow. Seems to work, but haven't looked further!
        val viewport = pane.viewportBounds
        val contentHeight = pane.content.boundsInLocal.height

        val bounds = pane.content.sceneToLocal(node.localToScene(node.getBoundsInLocal()));

        val nodeMinY = bounds.minY
        val nodeMaxY = bounds.maxY

        val viewportMinY = (contentHeight - viewport.height) * pane.vvalue
        val viewportMaxY = viewportMinY + viewport.height
        if (nodeMinY < viewportMinY) {
            pane.vvalue = nodeMinY / (contentHeight - viewport.height)
        } else if (nodeMaxY > viewportMaxY) {
            pane.vvalue = (nodeMaxY - viewport.height) / (contentHeight - viewport.height)
        }
    }

    private fun onCancel() {
        close()
    }

    private fun onOk() {
        if (checkAndRun()) {
            close()
        }
    }

    private fun onApply() {
        checkAndRun()
    }

    fun checkAndRun(): Boolean {

        // Are there any "dirty" fields, where the value in the GUI isn't in the Value.
        // For example, if a non-valid number is typed into a IntField
        form.descendants().forEach { field ->
            if (field.isDirty()) {
                ensureVisible(scrollPane, field)
                return false;
            }
        }

        val values = task.taskD.copyValues(values)

        if (check(values)) {
            run()
            return true
        }
        return false
    }

    fun check(values: Values): Boolean {

        form.descendants().forEach { field ->
            field.clearError()
        }

        try {
            task.taskD.root.check(values)
            task.check(values)

        } catch (e: ParameterException) {
            val field = form.findField(e.parameter)
            if (field != null) {
                field.showError(e.message!!)
                ensureVisible(scrollPane, field)
            }
            return false
        }
        return true
    }

    open fun run() {
        NewWindowTaskRunner( task.taskD.label + " Output").run(task, values)
    }

    open protected fun close() {
        stage?.let { it.hide() }
    }


    fun placeOnStage(stage: Stage) {
        this.stage = stage
        stage.title = task.taskD.label

        cancelButton.visibleProperty().set(true)

        val scene = Scene(root)

        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

}