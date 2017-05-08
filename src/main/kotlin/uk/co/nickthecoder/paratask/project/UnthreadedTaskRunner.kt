package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.util.AutoExit

open class UnthreadedTaskRunner(task: Task) : AbstractTaskRunner(task) {

    private var thread: Thread? = null

    override fun run() {

        pre()
        try {
            runTask()
        } finally {
            post()
        }

    }
}
