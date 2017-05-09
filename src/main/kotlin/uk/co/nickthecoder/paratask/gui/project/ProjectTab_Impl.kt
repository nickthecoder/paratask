package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.HidingSplitPane
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTab_Impl(override val tabs: ProjectTabs, toolPane: ToolPane)

    : ProjectTab, Tab() {

    override lateinit var projectTabs: ProjectTabs

    override var left: HalfTab = HalfTab_Impl(toolPane)

    override var right: HalfTab? = null

    val empty: Node = Label("<empty>")

    val stackPane = StackPane()

    val hidingSplitPane = HidingSplitPane(stackPane, left as Node, empty, Orientation.HORIZONTAL)

    init {
        hidingSplitPane.showJustLeft()
        setContent(stackPane)
        updateTab()
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

        hidingSplitPane.right = r
        hidingSplitPane.showBoth()
        r.attached(this)
        right = r
    }

    override fun remove(toolPane: ToolPane) {
        when (toolPane) {

            left.toolPane -> {
                if (right == null) {
                    projectTabs.removeTab( this )
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
        right = null
        hidingSplitPane.right = empty
        hidingSplitPane.left = left as Node
        hidingSplitPane.showJustLeft()
    }

    override fun split() {
        if (right == null) {
            add(left.toolPane.copy())
        }
    }

    override fun splitToggle() {
        if (right == null) {
            split()
        } else {
            right = null
            updateTab()

            hidingSplitPane.showJustLeft()
        }
    }

    override fun duplicateTab() {
        val newTab = tabs.addToolPane(left.toolPane.copy())

        right?.let { newTab.add(it.toolPane.copy()) }
    }

    override fun changed() {
        updateTab()
    }
}
