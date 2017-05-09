package uk.co.nickthecoder.paratask.project

import javafx.beans.property.BooleanProperty

interface TaskRunner {

    val listeners: List<TaskListener>

    val processors: MutableList<ResultProcessor>

    val disableRunProperty: BooleanProperty
    val showRunProperty: BooleanProperty
    val showStopProperty: BooleanProperty

    fun listen(listener: () -> Unit)

    fun run()

    fun hasStarted(): Boolean

    fun hasFinished(): Boolean

    fun isRunning(): Boolean
}
