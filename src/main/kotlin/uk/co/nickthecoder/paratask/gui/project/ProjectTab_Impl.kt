package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTab_Impl(override val tabs: ProjectTabs, toolPane: ToolPane)

    : ProjectTab, Tab() {

    val splitPane = SplitPane()

    override lateinit var projectTabs: ProjectTabs

    override var left: HalfTab = HalfTab_Impl(toolPane)

    override var right: HalfTab? = null

    init {
        setContent(splitPane)
        updateTab()
        splitPane.getItems().add(left as Node)
    }

    private fun updateTab() {
        setText(left.toolPane.tool.shortTitle())
        val imageView = left.toolPane.tool.icon?.let { ImageView(it) }
        setGraphic(imageView)
    }

    override fun attached(projectTabs: ProjectTabs) {
        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.attaching HalfTab")
        left.attached(this)
        ParaTaskApp.logAttach("ProjectTab.attached HalfTab")
    }

    override fun detaching() {
        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.detaching left HalfTab")
        left.detaching()
        ParaTaskApp.logAttach("ProjectTab.detached left HalfTab")

        right?.let {
            ParaTaskApp.logAttach("ProjectTab.detaching right HalfTab")
            it.detaching()
            ParaTaskApp.logAttach("ProjectTab.detached right HalfTab")
        }
    }

    override fun add(tool: Tool) {
        val toolPane = ToolPane_Impl(tool)
        add(toolPane)
    }

    override fun add(toolPane: ToolPane) {
        assert(right == null)

        val r = HalfTab_Impl(toolPane)

        r.attached(this)
        splitPane.getItems().add(toolPane as Node)
        right = r
    }

    override fun remove(toolPane: ToolPane) {
        val index = if (toolPane === left.toolPane) 0 else 1
        if (index == 1) {
            if (right == null) {
                throw RuntimeException("Attempted to remove a toolPane not belonging to this tab")
            }
            if (index == 1) {
                left = right!! // Must be in the JavaFX thread, so this can never fail
            }
        }
        right = null
        splitPane.getItems().removeAt(index)
    }

    override fun orientation(horizontal: Boolean) {
        splitPane.setOrientation(if (horizontal) Orientation.HORIZONTAL else Orientation.VERTICAL)
    }

    override fun split(horizontal: Boolean?) {
        horizontal?.let { orientation(it) }
        if (right == null) {

            val existingToolPane = left.toolPane
            val newTool = existingToolPane.tool.copy()
            val newToolPane = ToolPane_Impl(newTool)
            newToolPane.values = existingToolPane.values

            add(newToolPane)
        }
    }

    override fun splitToggle() {
        if (right == null) {
            split()
        } else {
            right = null
            updateTab()

            splitPane.getItems().removeAt(1)
        }
    }

    override fun changed() {
        updateTab()
    }
}
