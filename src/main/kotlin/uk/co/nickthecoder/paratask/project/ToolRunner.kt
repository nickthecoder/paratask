package uk.co.nickthecoder.paratask.project

import javafx.beans.property.BooleanProperty
import uk.co.nickthecoder.paratask.parameter.Values

interface ToolRunner {

    val disableRunProperty: BooleanProperty
    val showRunProperty: BooleanProperty
    val showStopProperty: BooleanProperty

    fun run(values: Values)

    fun hasStarted(): Boolean

    fun hasFinished(): Boolean

    fun isRunning(): Boolean
}
