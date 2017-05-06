package uk.co.nickthecoder.paratask.project

import javafx.beans.property.BooleanProperty

interface TaskRunner {

    val disableRunProperty: BooleanProperty
    val showRunProperty: BooleanProperty
    val showStopProperty: BooleanProperty

    fun run()

    fun hasStarted(): Boolean

    fun hasFinished(): Boolean

    fun isRunning(): Boolean
}
