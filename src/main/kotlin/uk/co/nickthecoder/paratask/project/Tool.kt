package uk.co.nickthecoder.paratask.project

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.project.ToolPane

interface Tool : Task {

    val toolRunner: ToolRunner

    var toolPane: ToolPane?

    fun shortTitle(): String

    val icon: Image?

    val optionsName: String

    var autoRun: Boolean

    /**
     * Note, this is separate from run because this must be done in JavaFX's thread, whereas run
     * will typically be done in its own thread.
     */
    fun updateResults()

    fun attached(toolPane: ToolPane)

    fun detaching()

    fun copy(): Tool

    fun creationString(): String

    companion object {
        fun create(creationString: String): Tool {
            return Class.forName(creationString).newInstance() as Tool
        }
    }
}
