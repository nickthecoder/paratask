package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class ToolPane(var tool: Tool) {

    private val splitPane = SplitPane()

    val node: Node = splitPane

    private var results: Node = HBox()

    private var parametersPane = ParametersPane(tool)

    private val resultsHolder = StackPane()

    lateinit var halfTab: HalfTab

    init {
        with(splitPane) {
            setOrientation(Orientation.VERTICAL)
            getItems().add(resultsHolder)
            val stackPane = StackPane()
            stackPane.children.add(parametersPane.node)
            getItems().add(stackPane)
        }
    }

    fun updateResults(results: Results) {
        resultsHolder.children.clear()
        resultsHolder.children.add(results.node)
    }

    fun attached(halfTab: HalfTab) {
        this.halfTab = halfTab

        ParaTaskApp.logAttach("ToolPane.attaching")
        parametersPane.attached(this)

        tool.attached(this)
        ParaTaskApp.logAttach("ToolPane.attached")
    }

    fun detaching() {
        ParaTaskApp.logAttach( "ToolPane detaching")
        parametersPane.detaching()
        tool.detaching()
        parametersPane.detaching()
        ParaTaskApp.logAttach( "ToolPane detached")
    }

}
