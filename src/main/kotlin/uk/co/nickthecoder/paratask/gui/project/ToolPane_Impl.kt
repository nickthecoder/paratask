package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, StackPane() {

    private val splitPane = SplitPane()

    private var results: Results = EmptyResults()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    private val resultsHolder = StackPane()

    private var dividerPosition: Double = 0.0

    override lateinit var halfTab: HalfTab

    init {
        with(splitPane) {

            setOrientation(Orientation.VERTICAL)
            getItems().add(resultsHolder)
            getItems().add(parametersPane as Node)
            dividerPosition = dividerPositions[0]
        }
        this.children.add(splitPane)
    }

    override var values: Values
        get() {
            return parametersPane.taskForm.values
        }
        set(v) {
            parametersPane.taskForm.values.copyValuesFrom(v)
        }

    override fun updateResults(results: Results) {
        if (results !== this.results) {
            results.detaching()
            resultsHolder.children.clear()

            this.results = results;
            resultsHolder.children.add(results.node)
            results.attached(this)
        }
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
        results.detaching()
        ParaTaskApp.logAttach("ToolPane detached")
    }

    override fun copy(): ToolPane {
        val newTool = tool.copy()
        val newToolPane = ToolPane_Impl(newTool)
        newToolPane.values = values

        return newToolPane
    }

    fun myRemoveAt(i: Int) {
        val node = splitPane.getItems().get(i)
        splitPane.getItems().removeAt(i)

        // Hide the node, and keep it in the scene graph, so that nodes can still perform getScene(),
        // even though it is not in the split pane any more.
        node.setVisible(false)
        this.children.add(node)
        println("Hiden ${node} but still in ${node.getScene()}")
    }

    override fun showJustResults() {
        if (splitPane.getItems().count() == 1) {
            myRemoveAt(0)
            splitPane.getItems().add(resultsHolder)
        } else if (splitPane.getItems().count() == 2) {
            dividerPosition = splitPane.dividerPositions[0]
            myRemoveAt(1)
        }
        results.chooseFocus(this).requestFocus()
    }

    override fun showJustParameters() {
        if (splitPane.getItems().count() == 1) {
            myRemoveAt(0)
            splitPane.getItems().add(parametersPane as Node)
        } else if (splitPane.getItems().count() == 2) {
            dividerPosition = splitPane.dividerPositions[0]
            myRemoveAt(0)
        }
        (parametersPane as Node).requestFocus()
    }

    override fun showBoth() {
        if (splitPane.getItems().count() != 2) {
            splitPane.getItems().clear()
            splitPane.getItems().add(resultsHolder)
            splitPane.getItems().add(parametersPane as Node)
            splitPane.setDividerPosition(0, dividerPosition)
        }
    }

    override fun toggleParameters() {
        if (splitPane.getItems().count() == 2) {
            showJustResults()
        } else {
            showBoth()
        }
    }

    override fun notBoth() {
        if (splitPane.getItems().count() == 2) {
            if (tool.toolRunner.hasStarted()) {
                showJustResults()
            } else {
                showJustParameters()
            }
        }
    }

    override fun cycle() {
        if (splitPane.getItems().count() == 2) {
            showJustResults()
        } else if (splitPane.getItems().get(0) == resultsHolder) {
            showJustParameters()
        } else {
            showBoth()
        }
    }
}
