package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

open class TaskPrompter(task: Task) : AbstractTaskPrompter(task) {

    val applyButton = Button("Apply")

    override fun onCancel() {
        task.taskRunner.cancel()
        close()
    }

    override fun onOk() {
        task.taskRunner.autoExit = true
        if (checkAndRun()) {
            close()
        }
    }

    private fun onApply() {
        checkAndRun()
    }

    override fun build() {
        super.build()

        with(okButton) {
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
        }

        with(applyButton)
        {
            onAction = EventHandler { onApply() }
            visibleProperty().bind(task.taskRunner.showRunProperty)
        }
        buttons.children.add(applyButton)
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
