package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

open class TaskPrompter(task: Task) : AbstractTaskPrompter(task) {

    val okButton = Button("OK")

    val cancelButton = Button("Cancel")

    val applyButton = Button("Apply")

    override fun build() {
        super.build()

        with(okButton) {
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
            onAction = EventHandler {
                onOk()
            }
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            cancelButtonProperty().set(true)
        }

        with(applyButton)
        {
            onAction = EventHandler { onApply() }
            visibleProperty().bind(task.taskRunner.showRunProperty)
        }

        buttons.children.addAll( okButton, cancelButton, applyButton)
    }

    fun onCancel() {
        task.taskRunner.cancel()
        close()
    }

    fun onOk() {
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
}
