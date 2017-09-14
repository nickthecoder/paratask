package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.ToolBar
import uk.co.nickthecoder.paratask.ToolBarTool

/**
 * Connects the Tool with its tool bar.
 */
class ToolBarToolConnector(val projectWindow: ProjectWindow, val tool: ToolBarTool, side: Side = Side.TOP) {

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
        /*
        toolBar.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
            println("Event $event")
            if (event.button == MouseButton.SECONDARY) {
                println("Showing menu")
                toolBar.contextMenu.show(toolBar, Side.BOTTOM, 0.0, 0.0)
                event.consume()
            }
        }
        */
        val editItem = MenuItem("Edit Toolbar")
        editItem.onAction = EventHandler { editToolBar() }
        toolBar.contextMenu.items.add(editItem)
    }

    fun remove() {
        projectWindow.removeToolBar(toolBar)
    }

    fun update() {
        val buttons = tool.toolBarButtons(projectWindow)

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
            halfTab.toolPane.parametersTab.isSelected = true
            return
        }
        val projectTab = projectWindow.addTool(tool)
        projectTab.left.toolPane.parametersTab.isSelected = true
    }

    inner class ConnectedToolBar : ToolBar() {
        val connector = this@ToolBarToolConnector
    }
}
