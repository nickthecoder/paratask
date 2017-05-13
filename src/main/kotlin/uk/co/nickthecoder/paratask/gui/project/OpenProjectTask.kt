package uk.co.nickthecoder.paratask.gui.project

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import javafx.application.Platform
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class OpenProjectTask() : AbstractTask() {
    override val taskD = TaskDescription("Open Project")

    val directory = FileParameter(
            "Projects Directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory)

    val name = ChoiceParameter<String?>("name", value = null)

    init {
        taskD.addParameters(directory, name)
        updateNameChoices()
        directory.listen { updateNameChoices() }
    }

    fun updateNameChoices() {
        name.clearChoices()
        val dir = directory.value
        if (dir == null) return

        println("Listing ${dir}")

        val lister = FileLister(extensions = listOf<String>("json"))
        for (file in lister.listFiles(dir)) {
            val label = file.nameWithoutExtension
            name.choice(label, label, label)
        }
    }

    override fun run() {
        val file = File(directory.value, name.value + ".json")
        Platform.runLater { load(file) }
    }

    private fun load(projectFile: File) {

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
