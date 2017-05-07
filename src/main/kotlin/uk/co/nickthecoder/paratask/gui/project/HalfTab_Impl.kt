package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.ButtonGroup
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.History
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.Tool

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab {

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
        backButton.disableProperty().bind(history.canUndoProperty.not())
        forwardButton.disableProperty().bind(history.canRedoProperty.not())
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
            add(Actions.TOOL_CLOSE.createButton(shortcuts) { onClose() })
        }

        bindButtons()
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

    fun bindButtons() {
        runButton.disableProperty().bind(toolPane.tool.taskRunner.disableRunProperty)
        runButton.visibleProperty().bind(toolPane.tool.taskRunner.showRunProperty)
        stopButton.visibleProperty().bind(toolPane.tool.taskRunner.showStopProperty)
    }

    override fun changeTool(tool: Tool) {
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)

        center = toolPane as Node
        toolPane.attached(this)

        projectTab.changed()

        bindButtons()
        history.push(tool)
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

    fun onClose() {
        projectTab.remove(toolPane)
    }

    override fun pushHistory() {
        history.push(toolPane.tool)
    }

    override fun pushHistory(tool: Tool) {
        history.push(tool)
    }
}
