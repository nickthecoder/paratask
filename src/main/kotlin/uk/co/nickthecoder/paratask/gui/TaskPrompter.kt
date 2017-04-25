package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values

open class TaskPrompter(val task: Task, val values: Values) {

    var root: BorderPane

    var form: ParametersForm

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

        form = ParametersForm(task.taskD.root, values)

        root.getStyleClass().add("task-prompter")

        val scrollPane = ScrollPane(form)
        scrollPane.fitToWidthProperty().set(true)

        root.center = scrollPane
        root.bottom = buttons
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
                // TODO LATER ensure the field is visible
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

        try {
            task.check(values)
        } catch (e: ParameterException) {
            val field = form.findField(e.parameter)
            if (field != null) {
                field.showError(e.message!!)
            }
            return false
        }
        return true
    }

    open fun run() {
        Thread({
            task.run(values)
        }).start()
    }

    open protected fun close() {
        stage?.let { it.hide() }
    }


    fun placeOnStage(stage: Stage) {
        this.stage = stage
        stage.title = task.taskD.title

        cancelButton.visibleProperty().set(true)

        val scene = Scene(root)

        val cssLocation = javaClass.getResource("paratask.css").toExternalForm()
        scene.getStylesheets().add(cssLocation)

        stage.setScene(scene)
        stage.show()
    }
}