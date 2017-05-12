package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool
import javax.swing.FocusManager

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, StackPane() {

    private val resultsTabs = mutableListOf<ResultsTab>()

    private val tabPane = TabPane()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    override lateinit var halfTab: HalfTab

    val parametersTab = Tab()

    init {

        // Adding the ParametersPane to the stack first (and then adding to the TabPane later) is a bodge
        // because TabPane doesn't set the parent of its child tabs immediately, and I need the ParametersPane
        // to be part of the Scene graph earlier than it otherwise would.
        children.addAll(tabPane, parametersPane as Node)
        tabPane.setSide(Side.BOTTOM)
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE)

        parametersTab.setText("Parameters")
        parametersTab.setContent(parametersPane as Node)

        tabPane.getTabs().addAll(parametersTab)
    }

    private fun removeOldResults() {
        for (resultsTab in resultsTabs) {
            resultsTab.results.detaching()
            tabPane.tabs.remove(resultsTab)
        }
    }

    override fun updateResults(vararg allResults: Results) {
        removeOldResults()

        var i = 0
        for (results in allResults) {
            children.add(results.node) // Temporarily add to StackPane. A bodge to ensure parent is set
            val resultsTab = ResultsTab(results)
            resultsTabs.add(resultsTab)
            tabPane.tabs.add(i, resultsTab)
            i++
            results.attached(this)
        }
        tabPane.selectionModel.select(0)
        if (allResults.size > 0) {
            allResults[0].focus()
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
        removeOldResults()
        ParaTaskApp.logAttach("ToolPane detached")
    }

    override fun toggleParameters() {
        if (parametersTab.isSelected()) {
            tabPane.getSelectionModel().select(0)
        } else {
            tabPane.getSelectionModel().select(tabPane.tabs.count() - 1)
        }
    }

    private class ResultsTab(val results: Results) : Tab(results.label, results.node) {}
}

