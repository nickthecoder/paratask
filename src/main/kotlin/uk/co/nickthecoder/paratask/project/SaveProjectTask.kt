package uk.co.nickthecoder.paratask.project

class SaveProjectTask(val projectWindow: ProjectWindow) : uk.co.nickthecoder.paratask.AbstractTask() {
    override val taskD = uk.co.nickthecoder.paratask.TaskDescription("Save Project")

    val directory = uk.co.nickthecoder.paratask.parameters.FileParameter("projectsDirectory", mustExist = null, expectFile = false)

    val name = uk.co.nickthecoder.paratask.parameters.StringParameter("name")

    val title = uk.co.nickthecoder.paratask.parameters.StringParameter("title")

    init {
        taskD.addParameters(directory, name, title)
        directory.value = projectWindow.projectFile?.parentFile ?: uk.co.nickthecoder.paratask.project.Preferences.projectsDirectory
        name.value = projectWindow.projectFile?.nameWithoutExtension() ?: ""
        title.value = projectWindow.title
    }

    override fun run() {
        val file = java.io.File(directory.value!!, name.value + ".json")
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

    fun save(projectFile: java.io.File) {
        val jroot = com.eclipsesource.json.JsonObject()

        jroot.set("title", title.value)
        jroot.set("width", projectWindow.scene.width)
        jroot.set("height", projectWindow.scene.height)

        val jtabs = com.eclipsesource.json.JsonArray()
        for (tab in projectWindow.tabs.listTabs()) {
            val jtab = com.eclipsesource.json.JsonObject()
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


        java.io.BufferedWriter(java.io.OutputStreamWriter(java.io.FileOutputStream(projectFile))).use {
            jroot.writeTo(it, com.eclipsesource.json.PrettyPrint.indentWithSpaces(4))
        }
    }

    private fun createHalfTab(halfTab: HalfTab): com.eclipsesource.json.JsonObject {
        val jhalfTab = com.eclipsesource.json.JsonObject()

        val tool = halfTab.toolPane.tool
        jhalfTab.set("tool", tool.creationString())

        val jparameters = com.eclipsesource.json.JsonArray()

        for (parameter in tool.taskD.root.descendants()) {
            if (parameter is uk.co.nickthecoder.paratask.parameters.ValueParameter<*>) {
                val jparameter = com.eclipsesource.json.JsonObject()
                jparameter.set("name", parameter.name)
                jparameter.set("value", parameter.stringValue)
                jparameters.add(jparameter)
            }
        }
        jhalfTab.add("parameters", jparameters)

        return jhalfTab
    }
}
