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

    val cancelButton = Button("Cancel")

    val applyButton = Button("Apply")

    init {
        task.taskRunner.processors.add(CommandInTerminalWindow(task.taskD.label))

        with(okButton) {
            onAction = EventHandler {
                onOk()
            }
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            cancelButtonProperty().set(true)
        }

        with(applyButton) {
            onAction = EventHandler { onApply() }
            visibleProperty().bind(task.taskRunner.showRunProperty)
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
        task.taskRunner.cancel()
        close()
    }

    private fun onOk() {
        task.taskRunner.autoExit = true
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
        task.taskRunner.run()
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