package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.Task

open class ThreadedTaskRunner(task: Task) : AbstractTaskRunner(task) {

    private var thread: Thread? = null

    override fun run() {

        runState = RunState.RUNNING

        thread = object : Thread("ThreadedToolRunner") {
            override fun run() {
                runTask()
            }
        }
        thread?.setDaemon(true)
        thread?.start()
    }

    open fun runTask() {
        task.run();
    }
}