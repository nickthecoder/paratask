package uk.co.nickthecoder.paratask.project

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.gui.project.ToolPane_Impl
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool() : Tool {

    override val toolRunner = ThreadedToolRunner(this)

    override var toolPane: ToolPane? = null

    override fun shortTitle() = taskD.name.uncamel()

    override val optionsName: String by lazy { taskD.name }

    override var autoRun: Boolean = true

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane

        if (autoRun) {
            toolPane.parametersPane.run(showJustResults = true)
        }
    }

    override val values: Values get() = ensureToolPane().values

    fun ensureToolPane(): ToolPane {
        return toolPane ?: ToolPane_Impl(this)
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

    override fun creationString(): String {
        return this::class.java.name
    }

}
