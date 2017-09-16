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
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, MyTabPane<ProjectTab_Impl>() {

    val addTabContextMenu = ContextMenu()

    val closedHistory = ClosedHistory()

    init {
        selectionModel.selectedItemProperty().addListener {
            _, oldTab, newTab ->
            onTabChanged(oldTab as ProjectTab_Impl?, newTab as ProjectTab_Impl?)
        }
        addEventHandler(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

        createAddTabButton { onAddTab() }
        buildContextMenu()
    }

    private fun onKeyPressed(event: KeyEvent) {
        tabs.forEach { tab ->
            if (tab.tabShortcut?.match(event) == true) {
                event.consume()
                tab.isSelected = true
            }
        }
    }

    private fun onTabChanged(oldTab: ProjectTab_Impl?, newTab: ProjectTab_Impl?) {
        oldTab?.deselected()
        newTab?.selected()
        newTab?.left?.toolPane?.tool?.let { projectWindow.toolChanged(it) }
    }

    override fun addTool(index: Int, tool: Tool, run: Boolean, select: Boolean): ProjectTab {
        val toolPane = ToolPane_Impl(tool)
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        add(index, newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")

        if (select) {
            selectionModel.clearAndSelect(index)
        }

        if (run) {
            try {
                tool.toolPane?.parametersPane?.run()
            } catch (e: Exception) {
            }
        } else {
            toolPane.halfTab.pushHistory()
        }
        return newProjectTab
    }

    override fun indexOf(projectTab: ProjectTab): Int {
        return tabs.indexOf(projectTab as MyTab)
    }

    override fun addTool(tool: Tool, run: Boolean, select: Boolean): ProjectTab {
        return addTool(tabs.size, tool, run = run, select = select)
    }


    override fun addAfter(after: ProjectTab, tool: Tool, run: Boolean, select: Boolean): ProjectTab {
        val index = indexOf(after)
        return addTool(index + 1, tool, run = run, select = select)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.selectedItem
    }

    override fun removeTab(projectTab: ProjectTab) {
        remove(projectTab as MyTab)
    }

    override fun remove(tab: MyTab) {
        val projectTab = (tab as ProjectTab)
        closedHistory.remember(projectTab)
        projectTab.detaching()
        super.remove(tab)
    }

    override fun restoreTab() {
        if (closedHistory.canRestore()) {
            closedHistory.restore(this)
        }
    }

    override fun split() {
        currentTab()?.split()
    }

    override fun splitToggle() {
        currentTab()?.splitToggle()
    }

    override fun duplicateTab() {
        currentTab()?.duplicateTab()
    }

    override fun listTabs(): List<ProjectTab> {
        return tabs.map { it }
    }

    override fun nextTab() {
        if (tabs.isNotEmpty()) {
            var index = selectionModel.selectedIndex + 1
            if (index >= tabs.size) index = 0

            selectionModel.clearAndSelect(index)
        }
    }

    override fun prevTab() {
        if (tabs.isNotEmpty()) {
            var index = selectionModel.selectedIndex - 1
            if (index < 0) index = tabs.size - 1

            selectionModel.clearAndSelect(index)
        }
    }

    override fun selectTab(index: Int) {
        if (index >= 0 && index < tabs.size) {
            selectionModel.clearAndSelect(index)
        }
    }

    override fun selectTab(projectTab: ProjectTab) {
        selectedTab = projectTab as ProjectTab_Impl
    }

    private fun buildContextMenu() {

        TaskRegistry.home.listTasks().filterIsInstance<Tool>().forEach { tool ->
            val imageView = tool.icon?.let { ImageView(it) }
            val item = MenuItem(tool.shortTitle, imageView)

            item.onAction = EventHandler {
                addTool(tool.copy())
            }
            addTabContextMenu.items.add(item)
        }
    }

    fun onAddTab() {
        addTabContextMenu.show(extraControl, Side.BOTTOM, 0.0, 0.0)
    }

}
