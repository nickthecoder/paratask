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

import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.ToolBar
import uk.co.nickthecoder.paratask.ToolBarTool

/**
 * Connects the Tool with its tool bar.
 */
class ToolBarToolConnector(
        val projectWindow: ProjectWindow,
        val tool: ToolBarTool,
        val runToolOnEdit: Boolean,
        side: Side = Side.TOP) {

    val toolBar = ConnectedToolBar()

    var side: Side = side
        set(v) {
            if (field != v) {
                field = v
                projectWindow.removeToolBar(toolBar)
                projectWindow.addToolBar(toolBar, v)

                toolBar.orientation = if (v == Side.TOP || v == Side.BOTTOM) Orientation.HORIZONTAL else Orientation.VERTICAL
            }
        }

    init {
        toolBar.contextMenu = ContextMenu()

        val editItem = MenuItem("Edit Toolbar")
        editItem.onAction = EventHandler { editToolBar() }
        toolBar.contextMenu.items.add(editItem)
    }

    fun remove() {
        projectWindow.removeToolBar(toolBar)
    }

    fun update(buttons: List<Button>) {

        if (buttons.isEmpty()) {
            projectWindow.removeToolBar(toolBar)
        } else {

            with(toolBar.items) {
                clear()
                buttons.forEach { button ->
                    add(button)
                }
            }

            if (toolBar.parent == null) {
                projectWindow.addToolBar(toolBar, side)
            }
        }
    }

    fun editToolBar() {
        tool.toolPane?.halfTab?.let { halfTab ->
            halfTab.projectTab.isSelected = true
            if (!runToolOnEdit) {
                halfTab.toolPane.parametersTab.isSelected = true
            }
            return
        }
        val projectTab = projectWindow.tabs.addTool(tool, run = runToolOnEdit)
        projectTab.left.toolPane.parametersTab.isSelected = true
    }

    inner class ConnectedToolBar : ToolBar() {
        val connector = this@ToolBarToolConnector
    }
}
