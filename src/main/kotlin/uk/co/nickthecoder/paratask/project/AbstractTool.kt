package uk.co.nickthecoder.paratask.project

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool() : Tool {

    override val toolRunner = ThreadedToolRunner(this)

    override var toolPane: ToolPane? = null

    override fun shortTitle() = taskD.name.uncamel()

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
    }

    override fun check(values: Values) {}

    override fun detaching() {
        this.toolPane = null
    }

    override val icon: Image? by lazy {
        ParaTaskApp.imageResource("tools/${iconName()}.png")
    }

    open fun iconName(): String = "${taskD.name}"

    override fun copy(): Tool {

        val copy = this::class.java.newInstance()

        return copy
    }
}
