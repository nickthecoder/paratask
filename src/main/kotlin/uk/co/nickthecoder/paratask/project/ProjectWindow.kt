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

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.tools.HomeTool
import uk.co.nickthecoder.paratask.tools.WebTool
import uk.co.nickthecoder.paratask.tools.editor.ExceptionTool
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class ProjectWindow(title: String = "", width: Double = 800.0, height: Double = 600.0) {

    var projectFile: File? = null

    private val borderPane = BorderPane()

    val scene = Scene(borderPane, width, height)

    private var stage: Stage? = null

    val tabs: ProjectTabs = ProjectTabs_Impl(this)

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper("ProjectWindow", borderPane)


    var title: String = ""

    init {
        this.title = title

        with(borderPane) {
            center = tabs as Node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        with(toolBar.items) {
            add(Actions.OPEN_PROJECT.createButton(shortcuts) { onOpenProject() })
            add(Actions.SAVE_PROJECT.createButton(shortcuts) { onSaveProject() })
            add(Actions.QUIT.createButton(shortcuts) { onQuit() })
            add(Actions.NEW_WINDOW.createButton(shortcuts) { onNewWindow() })
            add(Actions.NEW_TAB.createToolButton(shortcuts) { tool -> onNewTab(tool) })
            add(Actions.DUPLICATE_TAB.createButton(shortcuts) { tabs.duplicateTab() })
            add(Actions.SPLIT_TAB_TOGGLE.createButton(shortcuts) { tabs.splitToggle() })
            add(Actions.APPLICATION_ABOUT.createButton(shortcuts) { onAbout() })
        }
    }

    fun onQuit() {
        System.exit(0)
    }

    fun onNewWindow() {
        val newWindow = ProjectWindow("New Project")
        newWindow.placeOnStage(Stage())
        newWindow.addTool(HomeTool())
    }

    fun onNewTab(tool: Tool = HomeTool()) {
        addTool(tool.copy())
    }

    fun placeOnStage(stage: Stage) {
        GlobalShortcuts(scene, this)

        ParaTaskApp.style(scene)

        stage.title = title
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
        TaskPrompter(SaveProjectTask(this)).placeOnStage(Stage())
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

    companion object {

        fun load(projectFile: File) {

            val jroot = Json.parse(InputStreamReader(FileInputStream(projectFile))).asObject()

            val title = jroot.getString("title", "")
            val width = jroot.getDouble("width", 600.0)
            val height = jroot.getDouble("height", 600.0)

            val projectWindow = ProjectWindow(title, width, height)
            projectWindow.projectFile = projectFile
            projectWindow.title = jroot.getString("title", "")
            projectWindow.placeOnStage(Stage())

            val jtabs = jroot.get("tabs")
            jtabs?.let {
                for (jtab in jtabs.asArray().map { it.asObject() }) {

                    val jleft = jtab.get("left").asObject()
                    jleft?.let {
                        val tool = ProjectWindow.loadTool(jleft)
                        val projectTab = projectWindow.addTool(tool)

                        val jright = jtab.get("right")
                        if (jright != null) {
                            val toolR = ProjectWindow.loadTool(jright.asObject())
                            projectTab.split(toolR)
                        }
                    }
                }
            }
        }

        private fun loadTool(jhalfTab: JsonObject): Tool {
            val creationString = jhalfTab.get("tool").asString()
            val tool = Tool.create(creationString)

            val jparameters = jhalfTab.get("parameters").asArray()
            for (jparameter in jparameters.map { it.asObject() }) {
                val name = jparameter.get("name").asString()
                val value = jparameter.get("value").asString()
                val parameter = tool.taskD.root.find(name)
                if (parameter != null && parameter is ValueParameter<*>) {
                    parameter.stringValue = value
                }
            }
            return tool
        }
    }

}
