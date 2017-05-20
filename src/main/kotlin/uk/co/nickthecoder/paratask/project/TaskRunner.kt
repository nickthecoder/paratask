package uk.co.nickthecoder.paratask.project

import javafx.beans.property.BooleanProperty
import uk.co.nickthecoder.paratask.TaskListener

interface TaskRunner {

    val listeners: List<TaskListener>

    val processors: MutableList<ResultProcessor>

    val disableRunProperty: BooleanProperty
    val showRunProperty: BooleanProperty
    val showStopProperty: BooleanProperty

    var autoExit: Boolean

    fun listen(listener: (Boolean) -> Unit)

    fun run()

    fun cancel()

    fun hasStarted(): Boolean

    fun hasFinished(): Boolean

    fun isRunning(): Boolean

}
