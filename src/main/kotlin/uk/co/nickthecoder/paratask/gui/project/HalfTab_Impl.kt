package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ButtonGroup
import uk.co.nickthecoder.paratask.gui.ParentBodge
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.History
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.Tool

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab, ParentBodge {

    private val toolbar = ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = TextField()

    override lateinit var projectTab: ProjectTab

    val stopButton: Button

    val runButton: Button

    private val history = History(this)

    init {
        center = toolPane as Node
        bottom = toolbar

        optionsField.prefColumnCount = 6

        val splitGroup = ButtonGroup()
        with(splitGroup) {
            add(Actions.SPLIT_TOOL_TOGGLE.createButton(shortcuts) { toolPane.toggleParameters() })
            add(Actions.SPLIT_TOOL_CYCLE.createButton(shortcuts) { toolPane.cycle() })
        }

        val historyGroup = ButtonGroup()
        val backButton = Actions.HISTORY_BACK.createButton(shortcuts) { history.undo() }
        val forwardButton = Actions.HISTORY_FORWARD.createButton(shortcuts) { history.redo() }
        historyGroup.children.addAll(backButton, forwardButton)

        val runStopStack = StackPane()
        stopButton = Actions.TOOL_STOP.createButton(shortcuts) { onStop() }
        runButton = Actions.TOOL_RUN.createButton(shortcuts) { onRun() }
        runStopStack.children.addAll(stopButton, runButton)

        with(toolbar.getItems()) {
            add(optionsField)
            add(runStopStack)
            add(Actions.TOOL_SELECT.createToolButton(shortcuts) { tool -> onSelectTool(tool) })
            add(splitGroup)
            add(historyGroup)
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

    override fun changeTool(tool: Tool, values: Values?) {
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)
        if (values != null) {
            toolPane.values = values
        }
        center = toolPane as Node
        toolPane.attached(this)

        projectTab.changed()

        runButton.disableProperty().bind(tool.toolRunner.disableRunProperty)
        runButton.visibleProperty().bind(tool.toolRunner.showRunProperty)
        stopButton.visibleProperty().bind(tool.toolRunner.showStopProperty)
    }

    fun onStop() {
        val tool = toolPane.tool
        if (tool is Stoppable) {
            tool.stop()
        }
    }

    fun onRun() {
        toolPane.parametersPane.run()
    }

    fun onSelectTool(tool: Tool) {
        changeTool(tool.copy())
    }

    override fun pushHistory(tool: Tool, values: Values) {
        history.push(tool, values)
    }

    override fun parentBodge(): Parent? = projectTab.projectTabs as Parent
}
