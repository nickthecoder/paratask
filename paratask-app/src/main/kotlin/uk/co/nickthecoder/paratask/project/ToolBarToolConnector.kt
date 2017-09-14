package uk.co.nickthecoder.paratask.project

import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar
import uk.co.nickthecoder.paratask.ToolBarTool

/**
 * Connects the Tool with its tool bar.
 */
class ToolBarToolConnector(val projectWindow: ProjectWindow, val tool: ToolBarTool, side: Side = Side.TOP) {

    val toolButton: Button = ToolBarConnectorButton()

    val toolBar = ToolBar()

    var side: Side = side
        set(v) {
            if (field != v) {
                field = v
                projectWindow.removeToolBar(toolBar)
                projectWindow.addToolBar(toolBar, v)

                toolBar.orientation = if (v == Side.TOP || v == Side.BOTTOM) Orientation.HORIZONTAL else Orientation.VERTICAL
            }
        }

    fun remove() {
        projectWindow.removeToolBar(toolBar)
    }

    fun update() {
        with(toolBar.items) {
            clear()
            add(toolButton)
            add(Separator())

            tool.toolBarButtons(projectWindow).forEach {
                add(it)
            }
        }

        if (toolBar.parent == null) {
            projectWindow.addToolBar(toolBar, side)
        }
    }


    inner class ToolBarConnectorButton : ToolButton(projectWindow, tool, label = "", icon = tool.icon, newTab = false) {
        override fun onAction() {
            super.onAction()
            if (tool is ToolBarTool) {
                tool.toolBarConnector = this@ToolBarToolConnector
            }
        }
    }
}
