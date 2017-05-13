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

    //private val resultsTabs = mutableListOf<ResultsTab>()

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

    override fun resultsTool(): Tool {
        val tab = tabPane.selectionModel.selectedItem
        if (tab is ResultsTab) {
            return tab.results.tool
        } else {
            return tool
        }
    }

    private fun removeOldResults(oldResultsList: List<Results>): Int {
        var index = 0

        println("Removing some results ${oldResultsList.size}")

        // This is Order n squared, but n is small, so I won't bother optimising it!
        for (oldResults in oldResultsList) {
            val oldIndex = removeResults(oldResults)
            if (oldIndex >= 0) {
                index = oldIndex
            }
        }
        return index
    }

    private fun removeResults(results: Results): Int {
        var i: Int = 0
        for (tab in tabPane.tabs) {
            if (tab is ResultsTab && tab.results === results) {
                tabPane.tabs.removeAt(i)
                return i
            }
            i++
        }
        return -1
    }

    override fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>) {
        val replaceIndex = removeOldResults(oldResultsList)

        var index = replaceIndex
        for (results in resultsList) {
            children.add(results.node) // Temporarily add to StackPane. A bodge to ensure parent is set
            val resultsTab = ResultsTab(results)
            tabPane.tabs.add(index, resultsTab)
            index++
            results.attached(this)
        }
        tabPane.selectionModel.select(0)
        println("New tab index ${replaceIndex}")
        if (replaceIndex >= 0 && replaceIndex < tabPane.tabs.size) {
            val tab = tabPane.tabs[replaceIndex]
            if (tab is ResultsTab) {
                tabPane.selectionModel.select(replaceIndex)
                println("Focusing on ${tab.results}")
                tab.results.focus()
            }
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
        removeOldResults(tool.resultsList)
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

