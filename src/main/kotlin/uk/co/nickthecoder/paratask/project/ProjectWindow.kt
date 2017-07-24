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

import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.tools.HomeTool
import uk.co.nickthecoder.paratask.tools.WebTool
import uk.co.nickthecoder.paratask.tools.ExceptionTool
import uk.co.nickthecoder.paratask.util.AutoExit

class ProjectWindow(width: Double = 800.0, height: Double = 600.0) {

    val project = Project(this)

    private val borderPane = BorderPane()

    val scene = Scene(borderPane, width, height)

    private var stage: Stage? = null

    val tabs: ProjectTabs = ProjectTabs_Impl(this)

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper("ProjectWindow", borderPane)

    init {
        with(borderPane) {
            center = tabs as Node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        with(toolBar.items) {
            add(ParataskActions.OPEN_PROJECT.createButton(shortcuts) { onOpenProject() })
            add(ParataskActions.SAVE_PROJECT.createButton(shortcuts) { onSaveProject() })
            add(ParataskActions.QUIT.createButton(shortcuts) { onQuit() })
            add(ParataskActions.NEW_WINDOW.createButton(shortcuts) { onNewWindow() })
            add(ParataskActions.NEW_TAB.createToolButton(shortcuts) { tool -> onNewTab(tool) })
            add(ParataskActions.DUPLICATE_TAB.createButton(shortcuts) { tabs.duplicateTab() })
            add(ParataskActions.SPLIT_TAB_TOGGLE.createButton(shortcuts) { tabs.splitToggle() })
            add(ParataskActions.APPLICATION_ABOUT.createButton(shortcuts) { onAbout() })
        }
    }

    fun onQuit() {
        System.exit(0)
    }

    fun onNewWindow() {
        val newWindow = ProjectWindow()
        newWindow.placeOnStage(Stage())
        newWindow.addTool(HomeTool())
    }

    fun onNewTab(tool: Tool = HomeTool()) {
        val newTool = tool.copy()
        newTool.resolveParameters(project.resolver)
        addTool(newTool)
    }

    fun placeOnStage(stage: Stage) {
        GlobalShortcuts(scene, this)

        ParaTaskApp.style(scene)

        stage.title = "ParaTask"
        stage.scene = scene
        AutoExit.show(stage)
        this.stage = stage
    }

    fun addTool(tool: Tool): ProjectTab {
        return tabs.addTool(tool)
    }

    fun onAbout() {
        val about = WebTool("http://nickthecoder.co.uk/wiki/view/software/ParaTask")
        tabs.addTool(about)
    }

    fun onOpenProject() {
        TaskPrompter(OpenProjectTask()).placeOnStage(Stage())
    }


    fun onSaveProject() {
        project.save()
    }

    fun handleException(e: Exception) {
        try {
            val tool = ExceptionTool(e)
            addTool(tool)
        } catch(e: Exception) {
        }
    }

    fun toolChanged(currentTool: Tool) {
        stage?.titleProperty()?.bind(currentTool.longTitleProperty)
    }

}
