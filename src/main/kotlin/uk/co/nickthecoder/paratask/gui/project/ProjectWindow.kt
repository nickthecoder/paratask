package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ButtonGroup
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool
import uk.co.nickthecoder.paratask.util.AutoExit

class ProjectWindow() {

    private val borderPane = BorderPane()

    val tabs: ProjectTabs = ProjectTabs_Impl(this)

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper(borderPane)

    init {
        with(borderPane) {
            center = tabs as Node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        val splitGroup = ButtonGroup()
        with(splitGroup) {
            add(Actions.SPLIT_TOGGLE.createButton(shortcuts) { tabs.splitToggle() })
            add(Actions.SPLIT_VERTICAL.createButton(shortcuts) { tabs.split(horizontal = false) })
            add(Actions.SPLIT_HORIZONTAL.createButton(shortcuts) { tabs.split(horizontal = true) })
        }

        with(toolBar.getItems()) {
            add(Actions.QUIT.createButton(shortcuts) { onQuit() })
            add(Actions.NEW_WINDOW.createToolButton(shortcuts) { tool -> onNewWindow(tool) })
            add(Actions.NEW_TAB.createToolButton(shortcuts) { tool -> onNewTab(tool) })

            add(splitGroup)
        }
    }

    fun onQuit() {
        System.exit(0)
    }

    fun onNewWindow(tool: Tool) {
        println("New window")
        // TODO Implement new window
    }

    fun onNewTab(tool: Tool = HomeTool()) {
        addTool(tool.copy())
    }

    fun onChangeTool(tool: Tool) {
        // TODO Replace current tool
    }

    fun placeOnStage(stage: Stage) {

        val scene = Scene(borderPane)

        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

    fun addTool(tool: Tool) {
        tabs.addTool(tool)
    }

}