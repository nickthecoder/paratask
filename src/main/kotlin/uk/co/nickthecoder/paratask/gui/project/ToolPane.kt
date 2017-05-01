package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.project.Tool

class ToolPane(var tool: Tool) {

    private val splitPane = SplitPane()

    val node: Node = splitPane

    private var results: Node = HBox()

    private var parametersPane = ParametersPane(tool)

    private val resultsHolder = StackPane()

    lateinit var projectTab: ProjectTab

    init {
        with(splitPane) {
            setOrientation(Orientation.VERTICAL)
            getItems().add(resultsHolder)
            println("ToolPane adding parametersPane Node = ${parametersPane.node}")
            val stackPane = StackPane()
            stackPane.children.add(parametersPane.node)
            getItems().add(stackPane)
            println("ToolPane : ParametersPane parent = ${parametersPane.node.parent} gp = ${parametersPane.node?.parent?.parent}")
        }
    }

    fun updateResults(results: Results) {
        println("ToolPane updateResults ${results}")
        resultsHolder.children.clear()
        resultsHolder.children.add(results.node)
    }

    fun attached(projectTab: ProjectTab) {
        this.projectTab = projectTab

        println("ToolPane.attaching ParametersPane scene = ${splitPane.getScene()} and ${parametersPane.node.getScene()}")
        parametersPane.attached(this)
        println("ToolPane.attached ParametersPane")

        println("ToolPane.attaching Tool")
        tool.attached(this)
        println("ToolPane.attached Tool")
    }

    fun detatching() {
        parametersPane.detaching()
        tool.detaching()
        parametersPane.detaching()
    }

}
