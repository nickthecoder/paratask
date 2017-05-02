package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ButtonGroup
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.Tool

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab {

    private val toolbar = ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = TextField()

    override lateinit var projectTab: ProjectTab

    init {
        center = toolPane as Node
        bottom = toolbar

        optionsField.prefColumnCount = 6

        val splitGroup = ButtonGroup()
        with(splitGroup) {
            add(Actions.SPLIT_TOOL_TOGGLE.createButton(shortcuts) { toolPane.toggleParameters() })
            add(Actions.SPLIT_TOOL_CYCLE.createButton(shortcuts) { toolPane.cycle() })
        }

        with(toolbar.getItems()) {
            add(optionsField)
            add(splitGroup)
        }
    }

    override fun attached(projectTab: ProjectTab) {
        this.projectTab = projectTab

        ParaTaskApp.logAttach("HalfTab.attaching ToolPane")
        toolPane.attached(this)
        ParaTaskApp.logAttach("HalfTab.attached ToolPane")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("HalfTab.detaching ToolPane")
        toolPane.detaching()
        ParaTaskApp.logAttach("HalfTab.detached ToolPane")
    }

    override fun changeTool(tool: Tool) {
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)
        center = toolPane as Node
        toolPane.attached(this)

        projectTab.changed()
    }
}
