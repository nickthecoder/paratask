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

abstract class AbstractTaskPrompter(val task: Task) {

    var root = BorderPane()

    var taskForm = TaskForm(task)

    var stage: Stage? = null

    val buttons = FlowPane()

    val okButton = Button("OK")

    val cancelButton = Button("Cancel")

    open protected fun close() {
        stage?.let { it.hide() }
    }

    abstract protected fun onOk()

    abstract protected fun onCancel()

    open protected fun build() {

        task.taskRunner.processors.add(CommandInTerminalWindow(task.taskD.label))

        with(okButton) {
            onAction = EventHandler {
                onOk()
            }
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            cancelButtonProperty().set(true)
        }

        with(buttons) {
            styleClass.add("buttons")
            children.add(okButton)
            children.add(cancelButton)
        }

        with(root) {
            getStyleClass().add("task-prompter")
            center = taskForm.scrollPane
            bottom = buttons
        }
    }

    fun placeOnStage(stage: Stage) {

        build()

        this.stage = stage
        stage.title = task.taskD.label

        val scene = Scene(root)

        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

}
