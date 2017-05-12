package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool
import uk.co.nickthecoder.paratask.project.task.WebTool
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.File

class ProjectWindow() {

    var projectFile: File = Preferences.projectsDirectory

    private val borderPane = BorderPane()

    val scene = Scene(borderPane)

    val tabs: ProjectTabs = ProjectTabs_Impl(this)

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper("ProjectWindow", borderPane)

    init {
        with(borderPane) {
            center = tabs as Node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        with(toolBar.getItems()) {
            add(Actions.OPEN_PROJECT.createButton(shortcuts) { onOpenProject() })
            add(Actions.SAVE_PROJECT.createButton(shortcuts) { onSaveProject() })
            add(Actions.QUIT.createButton(shortcuts) { onQuit() })
            add(Actions.NEW_WINDOW.createToolButton(shortcuts) { tool -> onNewWindow(tool) })
            add(Actions.NEW_TAB.createToolButton(shortcuts) { tool -> onNewTab(tool) })
            add(Actions.DUPLICATE_TAB.createButton(shortcuts) { tabs.duplicateTab() })
            add(Actions.SPLIT_TAB_TOGGLE.createButton(shortcuts) { tabs.splitToggle() })
            add(Actions.APPLICATION_ABOUT.createButton(shortcuts) { onAbout() })
        }
    }

    fun onQuit() {
        System.exit(0)
    }

    fun onNewWindow(tool: Tool) {
        // TODO Implement new window
        println("TODO New Window for $tool}")
    }

    fun onNewTab(tool: Tool = HomeTool()) {
        addTool(tool.copy())
    }

    fun placeOnStage(stage: Stage) {


        ParaTaskApp.style(scene)

        stage.setScene(scene)
        AutoExit.show(stage)
    }

    fun addTool(tool: Tool): ProjectTab {
        return tabs.addTool(tool)
    }

    fun onAbout() {
        val about = WebTool("http://nickthecoder.co.uk/wiki/view/software/ParaTask")
        val toolPane = ToolPane_Impl(about)
        tabs.addToolPane(toolPane)
    }

    fun onOpenProject() {
        TaskPrompter(OpenProjectTask()).placeOnStage(Stage())
    }

    fun onSaveProject() {
        TaskPrompter(SaveProjectTask(this)).placeOnStage(Stage())
    }


}