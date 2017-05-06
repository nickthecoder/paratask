package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.field.TaskForm
import uk.co.nickthecoder.paratask.util.AutoExit

open class TaskPrompter(val task: Task) {

    var root = BorderPane()

    var taskForm = TaskForm(task)

    var stage: Stage? = null

    val okButton = Button("OK")

    val cancelButton = Button( "Cancel")

    val applyButton = Button("Apply")

    init {

        with(okButton) {
            onAction = EventHandler {
                onOk()
            }
            defaultButtonProperty().set(true)
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            visibleProperty().set(false)
            cancelButtonProperty().set(true)
        }

        with(applyButton) {
            onAction = EventHandler { onApply() }
            //visibleProperty().set(false)
        }

        val buttons = FlowPane()
        with(buttons) {
            styleClass.add("buttons")
            children.add(okButton)
            children.add(cancelButton)
            children.add(applyButton)
        }

        with(root) {
            getStyleClass().add("task-prompter")
            center = taskForm.scrollPane
            bottom = buttons
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

        if (taskForm.check()) {
            run()
            return true
        }
        return false
    }

    open fun run() {
        TerminalWindowTaskRunner(task.taskD.label + " Output").run(task)
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