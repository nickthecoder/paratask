package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

open class ProgrammingModeTaskPrompter(task: Task) : AbstractTaskPrompter(task) {

    override protected fun onOk() {
        // println("Programming mode task : ${task}")
        if (taskForm.check()) {
            close()
        }
    }

    override protected fun onCancel() {
        close()
    }
}
