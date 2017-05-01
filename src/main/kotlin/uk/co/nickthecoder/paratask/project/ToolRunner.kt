package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.parameter.Values

interface ToolRunner {
    fun run(values: Values)

    fun hasStarted(): Boolean

    fun hasFinished(): Boolean

    fun isRunning(): Boolean
}
