package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

open class ProgrammingModeTaskPrompter(task: Task) : AbstractTaskPrompter(task) {

    init {
        task.taskD.programmingMode = true
    }

    override protected fun onOk() {
        if (taskForm.check()) {
            close()
        }
    }

    override protected fun onCancel() {
        close()
    }
}
