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
import uk.co.nickthecoder.paratask.CompoundParameterResolver
import uk.co.nickthecoder.paratask.DirectoryResolver
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.JsonHelper
import java.io.*

class Project(val projectWindow: ProjectWindow) {

    var projectFile: File? = null

    val projectDataP = TaskRegistry.projectData.copy()

    val directoryResolver = object : DirectoryResolver() {
        override fun directory() = findProjectData("directory") as File
    }

    val resolver = CompoundParameterResolver(directoryResolver)

    val saveProjectTask: SaveProjectTask by lazy { SaveProjectTask(this) }

    fun findProjectData(name: String): Any? {
        val parameter = projectDataP.find(name)

        if (parameter is ValueParameter<*>) {
            return parameter.value
        }

        return null
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

        val jtabs = JsonArray()
        for (tab in projectWindow.tabs.listTabs()) {
            val jtab = JsonObject()
            jtabs.add(jtab)

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


            val jprojectData = jroot.get("projectData")
            if (jprojectData != null) {
                JsonHelper.read(jprojectData.asArray(), project.projectDataP)
            }

            val jtabs = jroot.get("tabs")
            jtabs?.let {
                for (jtab in jtabs.asArray().map { it.asObject() }) {

                    val jleft = jtab.get("left").asObject()
                    jleft?.let {
                        val tool = loadTool(jleft)
                        val projectTab = projectWindow.addTool(tool)

                        val jright = jtab.get("right")
                        if (jright != null) {
                            val toolR = loadTool(jright.asObject())
                            projectTab.split(toolR)
                        }
                    }
                }
            }

            return project
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

