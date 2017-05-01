package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.TerminalTool
import uk.co.nickthecoder.paratask.project.tasks.GrepTask
import uk.co.nickthecoder.paratask.util.AutoExit

class ProjectWindow() {

    private val borderPane = BorderPane()

    val node: Parent = borderPane

    private val tabs = ProjectTabs(this)

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper(node)

    init {
        with(borderPane) {
            center = tabs.node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        with(toolBar.getItems()) {
            add(Actions.QUIT.createButton(shortcuts) { onQuit() })
            add(Actions.NEW_WINDOW.createButton(shortcuts) { onNewWindow() })
            add(Actions.NEW_TAB.createButton(shortcuts) { onNewTab() })
        }
    }

    fun onQuit() {
        System.exit(0)
    }

    fun onNewWindow() {
        println("New window")
        // TODO Implement new window
    }

    fun onNewTab() {
        // TODO Replace with the home tool when its written
        addTool(TerminalTool(GrepTask()))
    }

    fun placeOnStage(stage: Stage) {

        val scene = Scene(node)

        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

    fun addTool(tool: Tool) {
        tabs.addTool(tool)
    }
}