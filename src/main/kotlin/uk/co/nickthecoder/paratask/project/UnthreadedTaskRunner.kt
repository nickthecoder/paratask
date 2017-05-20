package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task

open class UnthreadedTaskRunner(task: Task) : AbstractTaskRunner(task) {

    override fun run() {

        pre()
        try {
            runTask()
        } finally {
            post()
        }

    }
}
