package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task

open class ProgrammingModeTaskPrompter(task: Task) : AbstractTaskPrompter(task) {

    override fun onOk() {
        // println("Programming mode task : ${task}")
        if (taskForm.check()) {
            close()
        }
    }

    override fun onCancel() {
        close()
    }
}
