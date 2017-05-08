package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.util.AutoExit

open class ThreadedTaskRunner(task: Task) : AbstractTaskRunner(task) {

    override fun run() {
        AutoExit.inc()

        pre()

        val thread = object : Thread("ThreadedTaskRunner") {
            override fun run() {
                try {
                    runTask()
                } finally {
                    post()
                    AutoExit.dec()
                }
            }
        }
        thread.setDaemon(true)
        thread.start()
    }
}