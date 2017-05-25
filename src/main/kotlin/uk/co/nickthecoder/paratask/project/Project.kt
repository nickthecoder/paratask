package uk.co.nickthecoder.paratask.project

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.CompoundParameterResolver
import uk.co.nickthecoder.paratask.DirectoryResolver
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import java.io.*

class Project(val projectWindow: ProjectWindow) {

    var projectFile: File? = null

    var directory: File = File("").absoluteFile

    val directoryResolver = object : DirectoryResolver() {
        override fun directory() = directory
    }

    val resolver = CompoundParameterResolver(directoryResolver)

    /*
     Example JSON file :
    {
         "title" : "My Project",
         "width" : 600,
         "height" : 800,
         "directory" : "/home/me/myproject",
         "tabs" : [
             {
                 "left" = {
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

        this.projectFile = projectFile

        val jroot = JsonObject()

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

    companion object {

        fun load(projectFile: File): Project {

            val jroot = Json.parse(InputStreamReader(FileInputStream(projectFile))).asObject()

            val width = jroot.getDouble("width", 600.0)
            val height = jroot.getDouble("height", 600.0)

            val projectWindow = ProjectWindow(width, height)
            val project = projectWindow.project

            project.projectFile = projectFile

            projectWindow.placeOnStage(Stage())

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

