package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Tab

class ToolPane_Impl(override var tool: uk.co.nickthecoder.paratask.Tool)

    : ToolPane, javafx.scene.layout.StackPane() {

    private val tabPane = javafx.scene.control.TabPane()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    override lateinit var halfTab: HalfTab

    val parametersTab = javafx.scene.control.Tab()

    init {

        // Adding the ParametersPane to the stack first (and then adding to the TabPane later) is a bodge
        // because TabPane doesn't set the parent of its child tabs immediately, and I need the ParametersPane
        // to be part of the Scene graph earlier than it otherwise would.
        children.addAll(tabPane, parametersPane as javafx.scene.Node)
        tabPane.setSide(javafx.geometry.Side.BOTTOM)
        tabPane.setTabClosingPolicy(javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE)

        parametersTab.setText("Parameters")
        parametersTab.setContent(parametersPane as javafx.scene.Node)

        tabPane.getTabs().addAll(parametersTab)

        tabPane.selectionModel.selectedItemProperty().addListener(object : javafx.beans.value.ChangeListener<Tab> {
            override fun changed(ov: javafx.beans.value.ObservableValue<out Tab>, oldTab: javafx.scene.control.Tab?, newTab: javafx.scene.control.Tab?) {
                onTabChanged(oldTab, newTab)
            }
        })
    }

    override fun selected() {
        val tab = tabPane.selectionModel.selectedItem
        // TODO ResultsTab and parameters tab should be handled the same
        println()
        if (tab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab) {
            javafx.application.Platform.runLater() {
                tab.results.selected()
            }
        }
    }

    fun onTabChanged(oldTab: javafx.scene.control.Tab?, newTab: javafx.scene.control.Tab?) {
        if (oldTab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab) {
            oldTab.results.deselected()
        }
        if (newTab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab) {
            newTab.results.selected()
        }
    }

    override fun resultsTool(): uk.co.nickthecoder.paratask.Tool {
        val tab = tabPane.selectionModel.selectedItem
        if (tab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab) {
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
        var i: Int = 0
        for (tab in tabPane.tabs) {
            if (tab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab && tab.results === results) {
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
            val resultsTab = uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab(results)
            tabPane.tabs.add(index, resultsTab)
            index++
            results.attached(this)
        }
        tabPane.selectionModel.select(0)
        if (replaceIndex >= 0 && replaceIndex < tabPane.tabs.size) {
            val tab = tabPane.tabs[replaceIndex]
            if (tab is uk.co.nickthecoder.paratask.project.ToolPane_Impl.ResultsTab) {
                tabPane.selectionModel.select(replaceIndex)
            }
        }
    }

    override fun attached(halfTab: HalfTab) {
        this.halfTab = halfTab

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ToolPane.attaching")
        parametersPane.attached(this)

        tool.attached(this)
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ToolPane.attached")
    }

    override fun detaching() {
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ToolPane detaching")
        parametersPane.detaching()
        tool.detaching()
        parametersPane.detaching()
        removeOldResults(tool.resultsList)
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ToolPane detached")
    }

    override fun toggleParameters() {
        if (parametersTab.isSelected()) {
            tabPane.getSelectionModel().select(0)
        } else {
            tabPane.getSelectionModel().select(tabPane.tabs.count() - 1)
        }
    }

    private class ResultsTab(val results: Results) : javafx.scene.control.Tab(results.label, results.node) {
        init {
            this.textProperty().bind(results.labelProperty)
        }
    }
}

