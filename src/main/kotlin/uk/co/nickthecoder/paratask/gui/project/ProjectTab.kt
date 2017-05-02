package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool

class ProjectTab(val tabs: ProjectTabs, toolPane: ToolPane) : Tab() {

    val splitPane = SplitPane()

    lateinit var projectTabs: ProjectTabs

    var left: HalfTab = HalfTab(toolPane)

    var right: HalfTab? = null

    init {
        setContent(splitPane)
        setText(toolPane.tool.shortTitle())
        setGraphic(toolPane.tool.createIcon())

        splitPane.getItems().add(left.toolPane.node)
    }

    fun attached(projectTabs: ProjectTabs) {
        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.attaching HalfTab")
        left.attached(this)
        ParaTaskApp.logAttach("ProjectTab.attached HalfTab")
    }

    fun detaching() {
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

    fun add(tool: Tool) {
        val toolPane = ToolPane(tool)
        add(toolPane)
    }

    fun add(toolPane: ToolPane) {
        assert(right == null)

        val r = HalfTab(toolPane)

        r.attached(this)
        splitPane.getItems().add(toolPane.node)
        right = r
    }

    fun remove(toolPane: ToolPane) {
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

    fun orientation(horizontal: Boolean) {
        splitPane.setOrientation(if (horizontal) Orientation.HORIZONTAL else Orientation.VERTICAL)
    }

    fun split(horizontal: Boolean? = null) {
        horizontal?.let { orientation(it) }
        if (right == null) {
            // TODO Copy the existing tool
            add(HomeTool())
        }
    }

    fun splitToggle() {
        if (right == null) {
            split()
        } else {
            right = null
            setText(left.toolPane.tool.shortTitle())
            setGraphic(left.toolPane.tool.createIcon())

            splitPane.getItems().removeAt(1)
        }

    }
}
