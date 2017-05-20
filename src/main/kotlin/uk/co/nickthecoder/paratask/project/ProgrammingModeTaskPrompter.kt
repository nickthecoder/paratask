package uk.co.nickthecoder.paratask.project

open class ProgrammingModeTaskPrompter(task: uk.co.nickthecoder.paratask.Task) : AbstractTaskPrompter(task) {

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
