package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task

open class ThreadedTaskRunner(task: Task) : AbstractTaskRunner(task) {


    override fun run() {

        pre()

        val thread = object : Thread("ThreadedTaskRunner") {
            override fun run() {
                try {
                    runTask()
                } finally {
                    post()
                }
            }
        }
        thread.isDaemon = true
        thread.start()
    }
}