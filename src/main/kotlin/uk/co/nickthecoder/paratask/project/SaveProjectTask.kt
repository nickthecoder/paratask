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

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SaveProjectTask(val projectWindow: ProjectWindow) : AbstractTask() {
    override val taskD = TaskDescription("Save Project")

    val directory = FileParameter("projectsDirectory", mustExist = null, expectFile = false)

    val name = StringParameter("name")

    val title = StringParameter("title")

    init {
        taskD.addParameters(directory, name, title)
        directory.value = projectWindow.projectFile?.parentFile ?: Preferences.projectsDirectory
        name.value = projectWindow.projectFile?.nameWithoutExtension() ?: ""
        title.value = projectWindow.title
    }

    override fun run() {
        val file = File(directory.value!!, name.value + ".json")
        projectWindow.projectFile = file

        save(file)
    }

/*
 Example JSON file :
{
     "title" : "My Project",
     "tabs" : [
         {
             "left" = {
                "label" : "My Tab",
                "tool" : "uk.co.nickthecoder.paratask.whatever",
                "parameters" : [
                     { "name" : "foo", "value" : "fooValue" },
                     { "name" : "bar", "value" : "barValue" },
                 }
             }
         }
     ]
}
 */

    fun save(projectFile: File) {
        val jroot = JsonObject()

        jroot.set("title", title.value)
        jroot.set("width", projectWindow.scene.width)
        jroot.set("height", projectWindow.scene.height)

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

        val jparameters = JsonArray()

        for (parameter in tool.valueParameters()) {
            val jparameter = JsonObject()
            jparameter.set("name", parameter.name)
            jparameter.set("value", parameter.stringValue)
            jparameters.add(jparameter)
        }
        jhalfTab.add("parameters", jparameters)

        return jhalfTab
    }
}
