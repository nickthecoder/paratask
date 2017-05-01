package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.AutoExit

class ProjectWindow() {

    val node: Parent by lazy {
        val result = BorderPane()
        result.center = tabs.node
        result.setPrefSize(800.0, 600.0)
        result
    }

    val tabs = ProjectTabs(this)

    fun placeOnStage(stage: Stage) {

        val scene = Scene(node)

        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

    fun addTool(tool: Tool) {
        tabs.addTool(tool)
    }
}