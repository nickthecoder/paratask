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
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.table.RowFilter
import uk.co.nickthecoder.paratask.table.TableTool
import uk.co.nickthecoder.paratask.util.focusNext

class ToolPane_Impl(override var tool: Tool)

    : ToolPane, BorderPane() {

    override val tabPane = MyTabPane<MinorTab>()

    override var parametersPane: ToolParametersPane = ToolParametersPane_Impl(tool)

    override lateinit var halfTab: HalfTab

    override val parametersTab = ParametersTab(parametersPane)

    val header: HeaderOrFooter? = tool.createHeader()

    val footer: HeaderOrFooter? = tool.createFooter()

    var filterTab: FilterTab? = null

    init {
        center = tabPane

        tabPane.side = Side.BOTTOM

        parametersTab.canClose = false

        // Add the filter tab, if the tool has a filter
        val ttool = tool
        if (ttool is TableTool<*>) {
            val filter = ttool.rowFilter
            if (filter != null) {
                filterTab = FilterTab(ttool, filter)
                tabPane.add(filterTab!!)
            }
        }

        tabPane.add(parametersTab)

        tabPane.selectionModel.selectedItemProperty().addListener { _, oldTab, newTab -> onTabChanged(oldTab, newTab) }
    }

    /**
     * Called when the MAJOR tab has changed, and is used to ensure focus on the currently selected MINOR tab.
     */
    override fun selected() {
        val tab = tabPane.selectionModel.selectedItem

        if (tab is MinorTab) {
            Platform.runLater {
                tab.focus()
            }
        }
    }

    fun onTabChanged(oldTab: MyTab?, newTab: MyTab?) {
        if (oldTab is MinorTab) {
            oldTab.deselected()
        }
        if (newTab is MinorTab) {
            newTab.selected()
        }
        top = if (newTab is ResultsTab) header else null
        bottom = if (newTab is ResultsTab) footer else null
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
                tabPane.remove(tab)
                return i
            }
        }
        return -1
    }

    override fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>) {
        removeOldResults(oldResultsList)

        resultsList.forEach { results ->
            addResults(results)
        }
        // Select the first tab, unless another tab selected itself while being added.
        if (parametersTab.isSelected || filterTab?.isSelected == true) {
            tabPane.selectionModel.select(0)
        }
    }

    override fun addResults(results: Results): ResultsTab {
        tabPane.tabs.forEachIndexed { index, tab ->
            if (tab !is ResultsTab) {
                return addResults(results, index)
            }
        }
        return addResults(results, 0)
    }

    override fun addResults(results: Results, index: Int): ResultsTab {
        val resultsTab = ResultsTab(results)
        resultsTab.canClose = results.canClose

        tabPane.add(index, resultsTab)
        results.attached(resultsTab, this)
        return resultsTab
    }

    private var attached: Boolean = false

    override fun isAttached(): Boolean {
        return attached
    }

    override fun attached(halfTab: HalfTab) {
        this.halfTab = halfTab

        ParaTaskApp.logAttach("ToolPane.attaching")
        parametersPane.attached(this)
        filterTab?.parametersPane?.attached(this)

        tool.attached(this)

        ParaTaskApp.logAttach("ToolPane.attached")
        attached = true
    }

    override fun detaching() {
        attached = false
        ParaTaskApp.logAttach("ToolPane detaching")
        parametersPane.detaching()
        filterTab?.parametersPane?.detaching()
        tool.detaching()
        parametersPane.detaching()
        removeOldResults(tool.resultsList)

        header?.detatching()

        ParaTaskApp.logAttach("ToolPane detached")
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
        if (header != null) {
            ParaTaskApp.logFocus("ToolPane_Implt focusHeader. header.focus()")
            header.focus()
        } else {
            val results = currentResults()
            if (results is ResultsWithHeader) {
                ParaTaskApp.logFocus("ToolPane_Implt focusHeader. results.headerRows.focus()")
                results.headerRows.focus()
            }
        }
    }

    override fun focusResults() {
        if (skipFocus) {
            println("ToolPane_Impl Skipped focus")
        } else {
            val tab = tabPane.selectionModel.selectedItem
            if (tab is MinorTab) {
                ParaTaskApp.logFocus("ToolPane_Implt focusResults. tab.focus()")
                tab.focus()
            }
        }
    }

    override var skipFocus: Boolean = false

    override fun currentResults(): Results? {
        val tab = tabPane.selectedTab
        if (tab is ResultsTab) {
            return tab.results
        }
        return null
    }

    class ParametersTab(val parametersPane: ToolParametersPane) : MinorTab("Parameters") {

        init {
            content = parametersPane as Node
        }

        override fun focus() {
            if (parametersPane.tool.toolPane?.skipFocus != true) {
                Platform.runLater {
                    ParaTaskApp.logFocus("ParametersTab.focus. parametersPane.focus()")
                    parametersPane.focus()
                }
            }
        }

        override fun selected() {
            if (parametersPane.tool.toolPane?.skipFocus != true) {
                ParaTaskApp.logFocus("ParametersTab.selected focus()")
                focus()
            }
        }

        override fun deselected() {
        }
    }

    class FilterTab(val tool: TableTool<*>, val filter: RowFilter<*>) : MinorTab("Filter") {

        val parametersPane: ParametersPane = ParametersPane_Impl(filter)

        init {
            content = parametersPane as Node
        }

        override fun selected() {

        }

        override fun focus() {
            if (tool.toolPane?.skipFocus != true) {
                Platform.runLater {
                    ParaTaskApp.logFocus("FilterTab.focus. parametersPane.focus()")
                    parametersPane.focus()
                }
            }
        }

        override fun deselected() {
        }
    }
}

