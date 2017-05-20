package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskListener
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.Stoppable

enum class RunState {
    IDLE, RUNNING, FINISHED;
}

abstract class AbstractTaskRunner(val task: Task)

    : TaskRunner {

    override var autoExit = false

    override val listeners = mutableListOf<TaskListener>()

    override val processors = mutableListOf<ResultProcessor>()

    override val disableRunProperty = SimpleBooleanProperty(false)

    override val showRunProperty = SimpleBooleanProperty(true)

    override val showStopProperty = SimpleBooleanProperty(false)

    protected var runState: RunState = RunState.IDLE
        set(value) {
            field = value
            Platform.runLater {
                // Must only change JavaFX properties in the application thread.
                disableRunProperty.set(value == RunState.RUNNING)
                val showStop = task is Stoppable && value == RunState.RUNNING
                showStopProperty.set(showStop)
                showRunProperty.set(!showStop)
            }
        }

    override fun listen(listener: (Boolean) -> Unit) {
        listeners.add(object : TaskListener {
            override fun ended(cancelled: Boolean) {
                listener(cancelled)
            }
        })
    }

    abstract override fun run()

    open protected fun pre() {
        if (autoExit) AutoExit.inc("ThreadedTaskRunner")

        if (runState == RunState.RUNNING) {
            // As a Task has state, it cannot safely be run more than once concurrently.
            throw RuntimeException("Already running.")
        }
        task.check()
        runState = RunState.RUNNING
    }

    open protected fun post() {
        runState = RunState.FINISHED

        for (listener in listeners) {
            listener.ended(false)
        }
        if (autoExit) AutoExit.dec("ThreadedTaskRunner")

    }

    override fun cancel() {
        for (listener in listeners) {
            listener.ended(true)
        }
    }

    open protected fun runTask() {
        val result = task.run()

        processors.find { it.process(result) }
    }

    override fun hasStarted() = runState != RunState.IDLE

    override fun hasFinished() = runState == RunState.FINISHED

    override fun isRunning() = runState == RunState.RUNNING

}
