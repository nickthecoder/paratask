package uk.co.nickthecoder.paratask.project

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool(override val task: Task) : Tool {

    override val toolRunner = ThreadedToolRunner(this)

    override var toolPane: ToolPane? = null

    override fun shortTitle() = task.taskD.name.uncamel()

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
    }

    override fun detaching() {
        this.toolPane = null
    }

    override val icon: Image? by lazy {
        ParaTaskApp.imageResource("tools/${iconName()}.png")
    }

    open fun iconName(): String = "${task.taskD.name}"

    override fun copy(): Tool {
        // TODO Implement Tool.copy 
        return this
    }
}
