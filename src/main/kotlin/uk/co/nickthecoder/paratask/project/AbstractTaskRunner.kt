package uk.co.nickthecoder.paratask.project

import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.Task

enum class RunState {
    IDLE, RUNNING, FINISHED;
}

abstract class AbstractTaskRunner(val task: Task)

    : TaskRunner {

    override val listeners = mutableListOf<TaskListener>()

    override val processors = mutableListOf<ResultProcessor>()

    override val disableRunProperty = SimpleBooleanProperty(false)

    override val showRunProperty = SimpleBooleanProperty(true)

    override val showStopProperty = SimpleBooleanProperty(false)

    protected var runState: RunState = RunState.IDLE
        set(value) {
            field = value
            disableRunProperty.set(value == RunState.RUNNING)
            val showStop = task is Stoppable && value == RunState.RUNNING
            showStopProperty.set(showStop)
            showRunProperty.set(!showStop)
        }

    abstract override fun run()

    open protected fun pre() {
        task.check()
        runState = RunState.RUNNING
    }

    open protected fun post() {
        runState = RunState.FINISHED

        for (listener in listeners) {
            listener.ended()
        }
    }

    open protected fun runTask() {
        val result = task.run()

        for (processor in processors) {
            if (processor.process(result)) {
                break
            }
        }
    }

    override fun hasStarted() = runState != RunState.IDLE

    override fun hasFinished() = runState == RunState.FINISHED

    override fun isRunning() = runState == RunState.RUNNING
}
