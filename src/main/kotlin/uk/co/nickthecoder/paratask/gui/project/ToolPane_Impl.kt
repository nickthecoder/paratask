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

    private var results: Results = EmptyResults()

    private var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    private val resultsHolder = StackPane()

    private var dividerPosition: Double = 0.0

    override lateinit var halfTab: HalfTab

    init {
        setOrientation(Orientation.VERTICAL)
        getItems().add(resultsHolder)
        val stackPane = StackPane()
        stackPane.children.add(parametersPane as Node)
        getItems().add(stackPane)
        dividerPosition = dividerPositions[0]
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

    override fun showJustResults() {
        if (getItems().count() == 1) {
            getItems().removeAt(0)
            getItems().add(resultsHolder)
        } else if (getItems().count() == 2) {
            dividerPosition = dividerPositions[0]
            getItems().removeAt(1)
        }
        results.chooseFocus(this).requestFocus()
    }

    override fun showJustParameters() {
        if (getItems().count() == 1) {
            getItems().removeAt(0)
            getItems().add(parametersPane as Node)
        } else if (getItems().count() == 2) {
            dividerPosition = dividerPositions[0]
            getItems().removeAt(0)
        }
        (parametersPane as Node).requestFocus()
    }

    override fun showBoth() {
        if (getItems().count() != 2) {
            getItems().clear()
            getItems().add(resultsHolder)
            getItems().add(parametersPane as Node)
            setDividerPosition(0, dividerPosition)
        }
    }

    override fun toggleParameters() {
        if (getItems().count() == 2) {
            showJustResults()
        } else {
            showBoth()
        }
    }

    override fun notBoth() {
        if (getItems().count() == 2) {
            if (tool.toolRunner.hasStarted()) {
                showJustResults()
            } else {
                showJustParameters()
            }
        }
    }

    override fun cycle() {
        if (getItems().count() == 2) {
            showJustResults()
        } else if (getItems().get(0) == resultsHolder) {
            showJustParameters()
        } else {
            showBoth()
        }
    }
}
