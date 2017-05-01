package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values

interface Tool {

    val task: Task

    val toolRunner: ToolRunner

    var toolPane: ToolPane?

    fun run(values: Values)

    fun shortTitle(): String

    /**
     * Note, this is separate from run because this must be done in JavaFX's thread, whereas run
     * will typically be done in its own thread.
     */
    fun createResults(): Results

    fun attached(toolPane: ToolPane)

    fun detaching()
}
