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

import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.MyTab
import uk.co.nickthecoder.paratask.util.MyTabPane

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, MyTabPane() {

    init {
        selectionModel.selectedItemProperty().addListener {
            _, oldTab, newTab ->
            onTabChanged(oldTab as ProjectTab_Impl?, newTab as ProjectTab_Impl?)
        }
    }

    private fun onTabChanged(oldTab: ProjectTab_Impl?, newTab: ProjectTab_Impl?) {
        oldTab?.deselected()
        newTab?.selected()
        newTab?.left?.toolPane?.tool?.let { projectWindow.toolChanged(it) }
    }

    private fun addTool(index: Int, tool: Tool, run: Boolean): ProjectTab {
        val toolPane = ToolPane_Impl(tool)
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        add(index, newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")

        selectionModel.clearAndSelect(index)

        toolPane.halfTab.pushHistory()
        if (run) {
            try {
                tool.check()
                tool.taskRunner.run()
            } catch (e: Exception) {
            }
        }
        return newProjectTab
    }

    override fun addTool(tool: Tool, run: Boolean): ProjectTab {
        return addTool(tabs.size, tool, run = run)
    }

    override fun addAfter(after: ProjectTab, tool: Tool, run: Boolean): ProjectTab {
        val index = tabs.indexOf(after as MyTab)
        return addTool(index + 1, tool, run = run)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.selectedItem as ProjectTab
    }

    override fun removeTab(projectTab: ProjectTab) {
        projectTab.detaching()
        remove(projectTab as MyTab)
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
        return tabs.map { it as ProjectTab }
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


}
