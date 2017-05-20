package uk.co.nickthecoder.paratask.project

abstract class AbstractTaskPrompter(val task: uk.co.nickthecoder.paratask.Task) {

    var root = javafx.scene.layout.BorderPane()

    var taskForm = uk.co.nickthecoder.paratask.parameters.fields.TaskForm(task)

    var stage: javafx.stage.Stage? = null

    val buttons = javafx.scene.layout.FlowPane()

    val okButton = javafx.scene.control.Button("OK")

    val cancelButton = javafx.scene.control.Button("Cancel")

    open protected fun close() {
        stage?.let { it.hide() }
    }

    abstract protected fun onOk()

    abstract protected fun onCancel()

    open protected fun build() {

        task.taskRunner.processors.add(CommandInTerminalWindow(task.taskD.label))

        with(okButton) {
            onAction = javafx.event.EventHandler {
                onOk()
            }
        }

        with(cancelButton) {
            onAction = javafx.event.EventHandler { onCancel() }
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

    fun placeOnStage(stage: javafx.stage.Stage) {

        build()

        this.stage = stage
        stage.title = task.taskD.label

        val scene = javafx.scene.Scene(root)

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.style(scene)

        stage.setScene(scene)
        uk.co.nickthecoder.paratask.util.AutoExit.Companion.show(stage)
    }

}
