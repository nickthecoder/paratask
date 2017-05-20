package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, javafx.scene.layout.StackPane() {

    private val tabPane = javafx.scene.control.TabPane()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    override lateinit var halfTab: HalfTab

    val parametersTab = Tab()

    init {

        // Adding the ParametersPane to the stack first (and then adding to the TabPane later) is a bodge
        // because TabPane doesn't set the parent of its child tabs immediately, and I need the ParametersPane
        // to be part of the Scene graph earlier than it otherwise would.
        children.addAll(tabPane, parametersPane as javafx.scene.Node)
        tabPane.side = Side.BOTTOM
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        parametersTab.text = "Parameters"
        parametersTab.content = parametersPane as javafx.scene.Node

        tabPane.tabs.addAll(parametersTab)

        tabPane.selectionModel.selectedItemProperty().addListener { _, oldTab, newTab -> onTabChanged(oldTab, newTab) }
    }

    override fun selected() {
        val tab = tabPane.selectionModel.selectedItem
        // TODO ResultsTab and parameters tab should be handled the same
        println()
        if (tab is ToolPane_Impl.ResultsTab) {
            Platform.runLater {
                tab.results.selected()
            }
        }
    }

    fun onTabChanged(oldTab: Tab?, newTab: Tab?) {
        if (oldTab is ToolPane_Impl.ResultsTab) {
            oldTab.results.deselected()
        }
        if (newTab is ToolPane_Impl.ResultsTab) {
            newTab.results.selected()
        }
    }

    override fun resultsTool(): Tool {
        val tab = tabPane.selectionModel.selectedItem
        if (tab is ToolPane_Impl.ResultsTab) {
            return tab.results.tool
        } else {
            return tool
        }
    }

    private fun removeOldResults(oldResultsList: List<Results>): Int {
        var index = 0

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
        for ((i, tab) in tabPane.tabs.withIndex()) {
            if (tab is ToolPane_Impl.ResultsTab && tab.results === results) {
                tabPane.tabs.removeAt(i)
                return i
            }
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
        if (replaceIndex >= 0 && replaceIndex < tabPane.tabs.size) {
            val tab = tabPane.tabs[replaceIndex]
            if (tab is ToolPane_Impl.ResultsTab) {
                tabPane.selectionModel.select(replaceIndex)
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
        if (parametersTab.isSelected) {
            tabPane.selectionModel.select(0)
        } else {
            tabPane.selectionModel.select(tabPane.tabs.count() - 1)
        }
    }

    private class ResultsTab(val results: Results) : javafx.scene.control.Tab(results.label, results.node) {
        init {
            this.textProperty().bind(results.labelProperty)
        }
    }
}

