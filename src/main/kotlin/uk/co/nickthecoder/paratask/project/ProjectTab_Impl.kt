package uk.co.nickthecoder.paratask.project

class ProjectTab_Impl(override val tabs: ProjectTabs, toolPane: ToolPane)

    : ProjectTab, javafx.scene.control.Tab() {

    override lateinit var projectTabs: ProjectTabs

    override var left: HalfTab = HalfTab_Impl(toolPane)

    override var right: HalfTab? = null

    val splitPane = javafx.scene.control.SplitPane()

    val stackPane = javafx.scene.layout.StackPane(splitPane)

    init {
        stackPane.children.add(left as javafx.scene.Node)
        splitPane.items.add(left as javafx.scene.Node)
        splitPane.orientation = javafx.geometry.Orientation.HORIZONTAL
        setContent(stackPane)
        updateTab()
    }

    private fun updateTab() {
        textProperty().bind(left.toolPane.tool.shortTitleProperty)
        val imageView = left.toolPane.tool.icon?.let { javafx.scene.image.ImageView(it) }
        setGraphic(imageView)
    }

    override fun attached(projectTabs: ProjectTabs) {
        this.projectTabs = projectTabs

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.attaching HalfTab")
        left.attached(this)
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.attached HalfTab")
    }

    override fun detaching() {
        this.projectTabs = projectTabs

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.detaching left HalfTab")
        left.detaching()
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.detached left HalfTab")

        right?.let {
            uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.detaching right HalfTab")
            it.detaching()
            uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTab.detached right HalfTab")
        }
    }

    internal fun selected() {
        left.toolPane.selected()
    }

    internal fun deselected() {

    }

    override fun add(tool: uk.co.nickthecoder.paratask.Tool) {
        assert(right == null)

        val r = HalfTab_Impl(ToolPane_Impl(tool))

        stackPane.children.add(r)
        splitPane.items.add(r)
        r.attached(this)
        right = r
    }

    override fun remove(toolPane: ToolPane) {
        when (toolPane) {

            left.toolPane -> {
                if (right == null) {
                    projectTabs.removeTab(this)
                    return
                } else {
                    left = right!! // Must be in the JavaFX thread, so this can never fail
                }
            }
            right?.toolPane -> {
                if (right == null) {
                    throw RuntimeException("Attempted to remove a toolPane not belonging to this tab")
                }
            }
            else -> {
                throw RuntimeException("Attempt to remove a toolPane not belonging to this tab.")
            }
        }
        splitPane.items.clear()
        right = null
        splitPane.items.add(left as javafx.scene.Node)
    }

    override fun split(tool: uk.co.nickthecoder.paratask.Tool) {
        if (right == null) {
            add(tool)
        }
    }

    override fun split() {
        split(left.toolPane.tool.copy())
    }

    override fun splitToggle() {

        right?.let {
            remove(it.toolPane)
            return
        }

        split()
    }

    override fun duplicateTab() {
        val newTab = tabs.addTool(left.toolPane.tool.copy())

        right?.let { newTab.add(it.toolPane.tool.copy()) }
    }

    override fun changed() {
        updateTab()
    }
}
