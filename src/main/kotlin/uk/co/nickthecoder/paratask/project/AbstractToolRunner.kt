package uk.co.nickthecoder.paratask.project

import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.parameter.Values

enum class RunState {
    IDLE, RUNNING, FINISHED;
}

abstract class AbstractToolRunner(val tool: Tool)

    : ToolRunner {

    override val disableRunProperty = SimpleBooleanProperty(false)

    override val showRunProperty = SimpleBooleanProperty(true)

    override val showStopProperty = SimpleBooleanProperty(false)

    protected var runState: RunState = RunState.IDLE
        set(value) {
            field = value
            disableRunProperty.set(value == RunState.RUNNING)
            val showStop = tool is Stoppable && value == RunState.RUNNING
            showStopProperty.set(showStop)
            showRunProperty.set(!showStop)
        }

    abstract override fun run(values: Values)

    override fun hasStarted() = runState != RunState.IDLE

    override fun hasFinished() = runState == RunState.FINISHED

    override fun isRunning() = runState == RunState.RUNNING
}
