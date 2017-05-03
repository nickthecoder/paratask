package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool() : Tool {

    override val toolRunner = ThreadedToolRunner(this)

    override var toolPane: ToolPane? = null

    override fun shortTitle() = taskD.name.uncamel()

    override var autoRun: Boolean = true

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
        if (autoRun) {
            println("Autorunning. Scene = ${(toolPane as Node).getScene()}")
            // I run later, because of a bug in SplitPane, whereby it does NOT set the parent of its children
            // straight away, and therefore getScene can return null when it shouldn't. Hopefully the runLater
            // will give the parent SplitPanes the time to get their act together!
            Platform.runLater {
                println("Autorunning. (later) Scene = ${(toolPane as Node).getScene()}")
                toolPane.parametersPane.run()
            }
        }
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
