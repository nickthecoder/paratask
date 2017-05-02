package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, SplitPane() {

    private var results: Node = HBox()

    private var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    private val resultsHolder = StackPane()

    override lateinit var halfTab: HalfTab

    init {
        setOrientation(Orientation.VERTICAL)
        getItems().add(resultsHolder)
        val stackPane = StackPane()
        stackPane.children.add(parametersPane as Node)
        getItems().add(stackPane)
    }

    override var values: Values
        get() {
            return parametersPane.taskForm.values
        }
        set(v) {
            parametersPane.taskForm.values.copyValuesFrom(v)
        }

    override fun updateResults(results: Results) {
        resultsHolder.children.clear()
        resultsHolder.children.add(results.node)
    }

    override fun attached(halfTab: HalfTab) {
        this.halfTab = halfTab

        ParaTaskApp.logAttach("ToolPane.attaching")
        parametersPane.attached(this)

        tool.attached(this)
        ParaTaskApp.logAttach("ToolPane.attached")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("ToolPane detaching")
        parametersPane.detaching()
        tool.detaching()
        parametersPane.detaching()
        ParaTaskApp.logAttach("ToolPane detached")
    }

}
