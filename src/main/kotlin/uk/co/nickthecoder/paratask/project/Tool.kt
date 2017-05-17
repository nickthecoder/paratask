package uk.co.nickthecoder.paratask.project

import javafx.beans.property.StringProperty
import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.CopyableTask
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.project.option.OptionsRunner

interface Tool : CopyableTask {

    var toolPane: ToolPane?

    val shortTitleProperty: StringProperty

    val shortTitle: String

    val icon: Image?

    val optionsName: String

    var autoRun: Boolean

    val optionsRunner: OptionsRunner

    var resultsList: List<Results>

    /**
     * Note, this is separate from run because this must be done in JavaFX's thread, whereas run
     * will typically be done in its own thread.
     */
    fun createResults(): List<Results>

    fun updateResults()

    fun attached(toolPane: ToolPane)

    fun detaching()

    override fun copy(): Tool = super.copy() as Tool

    companion object {
        fun create(creationString: String): Tool {
            return CopyableTask.create(creationString) as Tool
        }
    }
}
