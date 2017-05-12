package uk.co.nickthecoder.paratask.gui.project

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SaveProjectTask(val projectWindow: ProjectWindow) : AbstractTask() {
    override val taskD = TaskDescription("Open Project")

    val directory = FileParameter("projectsDirectory", mustExist = null, expectFile = false, value = projectWindow.projectFile)

    val name = StringParameter("name")

    init {
        taskD.addParameters(directory, name)
    }

    override fun run() {
        val file = File(directory.value!!, name.value + ".json")
        projectWindow.projectFile = file

        save(file)
    }

/*
 Example JSON file :
{
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

        for (parameter in tool.taskD.root.descendants()) {
            if (parameter is ValueParameter<*>) {
                val jparameter = JsonObject()
                jparameter.set("name", parameter.name)
                jparameter.set("value", parameter.stringValue)
                jparameters.add(jparameter)
            }
        }
        jhalfTab.add("parameters", jparameters)

        return jhalfTab
    }
}
