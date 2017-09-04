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
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.tools.ExceptionTool
import uk.co.nickthecoder.paratask.util.JsonHelper
import java.io.*

class Project(val projectWindow: ProjectWindow) {

    var projectFile: File? = null

    var projectDirectoryP = FileParameter("directory", expectFile = false, required = true, value = File("").absoluteFile)

    val saveHistoryP = BooleanParameter("saveHistory", value = false)

    var projectDataP = GroupParameter("projectData")

    init {
        projectDataP.addParameters(projectDirectoryP, saveHistoryP)
    }

    val directoryResolver = object : DirectoryResolver() {
        override fun directory() = projectDirectoryP.value
    }

    val resolver = CompoundParameterResolver(directoryResolver)

    private val projectPreferences = mutableMapOf<String, Task>()

    val saveProjectTask: SaveProjectTask by lazy { SaveProjectTask(this) }

    fun storePreferences(task: Task) {
        projectPreferences[task.creationString()] = task.copy()
    }

    fun retrievePreferences(task: Task) {
        projectPreferences[task.creationString()]?.let {
            task.taskD.copyValuesFrom(it.taskD)
        }
    }

    fun save() {
        TaskPrompter(saveProjectTask).placeOnStage(Stage())
    }

/*
 Example JSON file :
{
     "title" : "My Project",
     "width" : 600,
     "height" : 800,
     "projectData" : [
         { "name" : "directory", "value" : "/home/me/myproject" },
         { "name" : "codeHeader", "value" : "/* ... */" }
     ],
     "tabs" : [
         {
            "tabTemplate"="{0}",
             "left" = {
                "tool" : "uk.co.nickthecoder.paratask.whatever",
                "parameters" : [
                     { "name" : "foo", "value" : "fooValue" },
                     { "name" : "bar", "value" : "barValue" }
                 ]
             }
         }
     ]
}
*/

    fun save(projectFile: File) {

        this.projectFile = projectFile

        val jroot = JsonObject()

        jroot.set("width", projectWindow.scene.width)
        jroot.set("height", projectWindow.scene.height)

        val jprojectData = JsonHelper.parametersAsJsonArray(projectDataP)
        jroot.add("projectData", jprojectData)

        val jprojectPreferences = JsonArray()
        jroot.add("preferences", jprojectPreferences)
        projectPreferences.values.forEach { pref ->
            val jpref = JsonHelper.taskAsJsonObject(pref)
            jprojectPreferences.add(jpref)
        }

        val jtabs = JsonArray()
        for (tab in projectWindow.tabs.listTabs()) {
            val jtab = JsonObject()
            jtabs.add(jtab)

            val jtabProperties = JsonHelper.parametersAsJsonArray((tab as ProjectTab_Impl).tabProperties)
            jtab.add("properties", jtabProperties)

            val jleft = createHalfTab(tab.left)
            jtab.set("left", jleft)

            val right = tab.right
            if (right != null) {
                val jright = createHalfTab(right)
                jtab.set("right", jright)
            }
        }
        jroot.add("tabs", jtabs)


        BufferedWriter(OutputStreamWriter(FileOutputStream(projectFile))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }
    }

    private fun createHalfTab(halfTab: HalfTab): JsonObject {
        val jhalfTab = JsonObject()

        val tool = halfTab.toolPane.tool
        jhalfTab.set("tool", tool.creationString())

        val jparameters = JsonHelper.parametersAsJsonArray(tool)
        jhalfTab.add("parameters", jparameters)

        if (saveHistoryP.value == true) {
            // Save the history of this half tab
            val history = halfTab.history.save()
            val jhistory = JsonArray()
            val jfuture = JsonArray()

            history.first.forEachIndexed { i, moment ->
                if (i != history.second) {
                    val jpart = if (i < history.second) jhistory else jfuture
                    val jitem = JsonObject()
                    jpart.add(jitem)
                    jitem.add("tool", moment.creationString)
                    jitem.add("parameters", JsonHelper.parametersAsJsonArray(moment.tool))
                }
            }

            if (!jhistory.isEmpty) {
                jhalfTab.add("history", jhistory)
            }
            if (!jfuture.isEmpty) {
                jhalfTab.add("future", jfuture)
            }
        }

        return jhalfTab
    }

    companion object {

        fun load(projectFile: File): Project {

            val jroot = Json.parse(InputStreamReader(FileInputStream(projectFile))).asObject()
            
            val width = jroot.getDouble("width", 600.0)
            val height = jroot.getDouble("height", 600.0)

            val projectWindow = ProjectWindow(width, height)
            projectWindow.placeOnStage(Stage())

            val project = projectWindow.project

            project.projectFile = projectFile

            try {
                val jprojectData = jroot.get("projectData")
                if (jprojectData != null) {
                    JsonHelper.read(jprojectData.asArray(), project.projectDataP)
                }
            } catch(e: Exception) {
                projectWindow.addTool(ExceptionTool(e))
            }

            try {
                val jprojectPreferences = jroot.get("preferences")
                if (jprojectPreferences != null) {
                    (jprojectPreferences as JsonArray).forEach { jpref ->
                        val task = JsonHelper.readTask(jpref as JsonObject)
                        project.storePreferences(task)
                    }
                }
            } catch(e: Exception) {
                projectWindow.addTool(ExceptionTool(e))
            }

            try {
                val jtabs = jroot.get("tabs")
                jtabs?.let {
                    for (jtab in jtabs.asArray().map { it.asObject() }) {

                        try {
                            val jleft = jtab.get("left").asObject()
                            jleft?.let {
                                val tool = loadTool(jleft)
                                val projectTab = projectWindow.addTool(tool, select = false)
                                updateHistory(jleft, projectTab.left)

                                val jright = jtab.get("right")
                                if (jright != null) {
                                    val toolR = loadTool(jright.asObject())
                                    projectTab.split(toolR)
                                    updateHistory(jright.asObject(), projectTab.right!!)
                                }

                                val jtabProperties = jtab.get("properties")
                                jtabProperties?.let {
                                    JsonHelper.read(jtabProperties as JsonArray, (projectTab as ProjectTab_Impl).tabProperties)
                                }
                            }
                        } catch (e: Exception) {
                            projectWindow.addTool(ExceptionTool(e))
                        }
                    }
                }
            } catch (e: Exception) {
                projectWindow.addTool(ExceptionTool(e))
            }

            return project
        }

        private fun updateHistory(jhalfTab: JsonObject, halfTab: HalfTab) {

            val jhistory = jhalfTab.get("history")
            val jfuture = jhalfTab.get("future")

            if (jhistory != null) {
                updatePartHistory(jhistory as JsonArray) { tool ->
                    halfTab.history.insertHistory(tool)
                }
            }

            if (jfuture != null) {
                updatePartHistory(jfuture as JsonArray) { tool ->
                    halfTab.history.addFuture(tool)
                }
            }
        }

        private fun updatePartHistory(jhistory: JsonArray, add: (Tool) -> Unit) {
            jhistory.forEach { jmoment ->
                val tool = loadTool(jmoment as JsonObject)
                add(tool)
            }
        }

        private fun loadTool(jhalfTab: JsonObject): Tool {
            val creationString = jhalfTab.get("tool").asString()
            val tool = TaskRegistry.createTool(creationString)

            val jparameters = jhalfTab.get("parameters")
            if (jparameters != null) {
                JsonHelper.read(jparameters.asArray(), tool)
            }

            return tool
        }
    }

}

