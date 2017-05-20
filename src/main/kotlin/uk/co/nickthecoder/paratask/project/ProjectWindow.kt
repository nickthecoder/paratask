package uk.co.nickthecoder.paratask.project

class ProjectWindow(title: String = "", width: Double = 800.0, height: Double = 600.0) {

    var projectFile: java.io.File? = null

    private val borderPane = javafx.scene.layout.BorderPane()

    val scene = javafx.scene.Scene(borderPane, width, height)

    private var stage: javafx.stage.Stage? = null

    val tabs: ProjectTabs = ProjectTabs_Impl(this)

    private val toolBar = javafx.scene.control.ToolBar()

    private val shortcuts = ShortcutHelper("ProjectWindow", borderPane)

    var title: String = ""

    init {
        this.title = title

        with(borderPane) {
            center = tabs as javafx.scene.Node
            top = toolBar
            setPrefSize(800.0, 600.0)
        }

        with(toolBar.getItems()) {
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
        val newWindow = uk.co.nickthecoder.paratask.project.ProjectWindow("New Project")
        newWindow.placeOnStage(javafx.stage.Stage())
        newWindow.addTool(uk.co.nickthecoder.paratask.tools.HomeTool())
    }

    fun onNewTab(tool: uk.co.nickthecoder.paratask.Tool = uk.co.nickthecoder.paratask.tools.HomeTool()) {
        addTool(tool.copy())
    }

    fun placeOnStage(stage: javafx.stage.Stage) {
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.style(scene)

        stage.title = title
        stage.setScene(scene)
        uk.co.nickthecoder.paratask.util.AutoExit.Companion.show(stage)
        this.stage = stage
    }

    fun addTool(tool: uk.co.nickthecoder.paratask.Tool): ProjectTab {
        return tabs.addTool(tool)
    }

    fun onAbout() {
        val about = uk.co.nickthecoder.paratask.tools.WebTool("http://nickthecoder.co.uk/wiki/view/software/ParaTask")
        val toolPane = ToolPane_Impl(about)
        tabs.addToolPane(toolPane)
    }

    fun onOpenProject() {
        uk.co.nickthecoder.paratask.project.TaskPrompter(uk.co.nickthecoder.paratask.gui.project.OpenProjectTask()).placeOnStage(javafx.stage.Stage())
    }


    fun onSaveProject() {
        uk.co.nickthecoder.paratask.project.TaskPrompter(uk.co.nickthecoder.paratask.gui.project.SaveProjectTask(this)).placeOnStage(javafx.stage.Stage())
    }

    companion object {

        fun load(projectFile: java.io.File) {

            val jroot = com.eclipsesource.json.Json.parse(java.io.InputStreamReader(java.io.FileInputStream(projectFile))).asObject()

            val title = jroot.getString("title", "")
            val width = jroot.getDouble("width", 600.0)
            val height = jroot.getDouble("height", 600.0)

            val projectWindow = uk.co.nickthecoder.paratask.project.ProjectWindow(title, width, height)
            projectWindow.projectFile = projectFile
            projectWindow.title = jroot.getString("title", "")
            projectWindow.placeOnStage(javafx.stage.Stage())

            val jtabs = jroot.get("tabs")
            jtabs?.let {
                for (jtab in jtabs.asArray().map { it.asObject() }) {

                    val jleft = jtab.get("left").asObject()
                    jleft?.let {
                        val tool = uk.co.nickthecoder.paratask.project.ProjectWindow.Companion.loadTool(jleft)
                        val projectTab = projectWindow.addTool(tool)

                        val jright = jtab.get("right")
                        if (jright != null) {
                            val toolR = uk.co.nickthecoder.paratask.project.ProjectWindow.Companion.loadTool(jright.asObject())
                            projectTab.split(toolR)
                        }
                    }
                }
            }
        }

        private fun loadTool(jhalfTab: com.eclipsesource.json.JsonObject): uk.co.nickthecoder.paratask.Tool {
            val creationString = jhalfTab.get("tool").asString()
            val tool = uk.co.nickthecoder.paratask.Tool.Companion.create(creationString)

            val jparameters = jhalfTab.get("parameters").asArray()
            for (jparameter in jparameters.map { it.asObject() }) {
                val name = jparameter.get("name").asString()
                val value = jparameter.get("value").asString()
                val parameter = tool.taskD.root.find(name)
                if (parameter != null && parameter is uk.co.nickthecoder.paratask.parameters.ValueParameter<*>) {
                    parameter.stringValue = value
                }
            }
            return tool
        }
    }

    fun handleException(e: Exception) {
        try {
            val tool = uk.co.nickthecoder.paratask.tools.editor.ExceptionTool(e)
            addTool(tool)
        } catch(e: Exception) {
        }
    }
}
