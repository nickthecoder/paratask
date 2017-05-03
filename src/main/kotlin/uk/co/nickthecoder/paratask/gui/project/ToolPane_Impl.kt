package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.HidingSplitPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, StackPane() {

    private var results: Results = EmptyResults()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    private val resultsHolder = StackPane()

    override val hidingSplitPane = HidingSplitPane(this, resultsHolder, parametersPane as Node)

    override lateinit var halfTab: HalfTab

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
        hidingSplitPane.showJustTop()
    }

    override fun showJustParameters() {
        hidingSplitPane.showJustBottom()
    }

    override fun showBoth() {
        hidingSplitPane.showBoth()
    }

    override fun toggleParameters() {
        hidingSplitPane.toggleBottom()
    }

    override fun notBoth() {
        if (hidingSplitPane.splitPane.getItems().count() == 2) {
            if (tool.toolRunner.hasStarted()) {
                hidingSplitPane.showJustTop()
            } else {
                hidingSplitPane.showJustBottom()
            }
        }
    }

    override fun cycle() {
        hidingSplitPane.cycle()
    }
}

