package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values

abstract class AbstractTool(override val task: Task) : Tool {

    override val toolRunner = ThreadedToolRunner(this)

    override var toolPane: ToolPane? = null

    override fun shortTitle() = task.taskD.name

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
    }

    override fun detaching() {
        this.toolPane = null
    }
}
