package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

interface ToolPane {

    var tool: Tool

    val halfTab: HalfTab

    fun updateResults(results: Results)

    fun attached(halfTab: HalfTab)

    fun detaching()

}
