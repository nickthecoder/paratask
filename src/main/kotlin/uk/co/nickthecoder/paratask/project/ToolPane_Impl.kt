/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, StackPane() {

    val borderPane = BorderPane()

    private val tabPane = TabPane()

    override var parametersPane: ParametersPane = ParametersPane_Impl(tool)

    override lateinit var halfTab: HalfTab

    val parametersTab = ParametersTab(parametersPane)

    val headerRowsBox = VBox()

    val headerRows: List<HeaderRow>

    init {
        borderPane.center = tabPane

        headerRows = tool.createHeaderRows()

        headerRowsBox.styleClass.add("header")

        // Adding the ParametersPane to the stack first (and then adding to the TabPane later) is a bodge
        // because TabPane doesn't set the parent of its child tabs immediately, and I need the ParametersPane
        // to be part of the Scene graph earlier than it otherwise would.
        children.addAll(borderPane, parametersPane as Node)
        tabPane.side = Side.BOTTOM
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        parametersTab.text = "Parameters"
        parametersTab.content = parametersPane as Node

        tabPane.tabs.addAll(parametersTab)

        tabPane.selectionModel.selectedItemProperty().addListener { _, oldTab, newTab -> onTabChanged(oldTab, newTab) }
    }

    override fun selected() {
        val tab = tabPane.selectionModel.selectedItem

        if (tab is MinorTab) {
            Platform.runLater {
                tab.focus()
            }
        }
    }

    fun onTabChanged(oldTab: Tab?, newTab: Tab?) {
        if (oldTab is MinorTab) {
            oldTab.deselected()
        }
        if (newTab is MinorTab) {
            newTab.selected()
            newTab.focus()
        }
        borderPane.top = if (newTab === parametersTab) null else if (headerRowsBox.children.isEmpty()) null else headerRowsBox

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
            if (tab is ResultsTab && tab.results === results) {
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
            if (tab is ResultsTab) {
                tabPane.selectionModel.select(replaceIndex)
            }
        }
    }

    override fun attached(halfTab: HalfTab) {
        this.halfTab = halfTab

        ParaTaskApp.logAttach("ToolPane.attaching")
        parametersPane.attached(this)

        tool.attached(this)

        headerRows.forEach() {
            headerRowsBox.children.add(it)
        }

        val lastRowIndex = headerRows.size - 1
        if (lastRowIndex >= 0) {
            val lastRow = headerRows[lastRowIndex]
            lastRow.addRunButton(tool, scene)
        }

        ParaTaskApp.logAttach("ToolPane.attached")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("ToolPane detaching")
        parametersPane.detaching()
        tool.detaching()
        parametersPane.detaching()
        removeOldResults(tool.resultsList)

        headerRows.forEach() {
            it.detaching()
        }

        ParaTaskApp.logAttach("ToolPane detached")
    }

    override fun toggleParameters() {
        if (parametersTab.isSelected) {
            tabPane.selectionModel.select(0)
        } else {
            tabPane.selectionModel.select(tabPane.tabs.count() - 1)
        }
    }

    override fun nextTab() {
        if (tabPane.tabs.isNotEmpty()) {
            var index = tabPane.selectionModel.selectedIndex + 1
            if (index >= tabPane.tabs.size) index = 0

            tabPane.selectionModel.clearAndSelect(index)
        }
    }

    override fun prevTab() {
        if (tabPane.tabs.isNotEmpty()) {
            var index = tabPane.selectionModel.selectedIndex - 1
            if (index < 0) index = tabPane.tabs.size - 1

            tabPane.selectionModel.clearAndSelect(index)
        }
    }

    override fun selectTab(index: Int) {
        if (index >= 0 && index < tabPane.tabs.size) {
            tabPane.selectionModel.clearAndSelect(index)
        }
    }

    override fun focusHeader() {
        if (headerRows.isNotEmpty()) {
            headerRows[0].focus()
        }
    }

    override fun focusResults() {
        val tab = tabPane.selectionModel.selectedItem
        if (tab is MinorTab) {
            tab.focus()
        }
    }


    class ParametersTab(val parametersPane: ParametersPane) : Tab(), MinorTab {
        override fun focus() {
            parametersPane.taskForm.form.focus()
        }

        override fun selected() {
            focus()
        }

        override fun deselected() {
        }
    }
}

