package uk.co.nickthecoder.paratask.gui.project

import javafx.application.Platform
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
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.AutoUpdater

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab {

    private val toolbar = ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = TextField()

    override lateinit var projectTab: ProjectTab

    val autoUpdater: AutoUpdater

    val stopButton: Button

    val runButton: Button

    init {
        center = toolPane as Node
        bottom = toolbar

        optionsField.prefColumnCount = 6

        val splitGroup = ButtonGroup()
        with(splitGroup) {
            add(Actions.SPLIT_TOOL_TOGGLE.createButton(shortcuts) { toolPane.toggleParameters() })
            add(Actions.SPLIT_TOOL_CYCLE.createButton(shortcuts) { toolPane.cycle() })
        }

        stopButton = Actions.TOOL_STOP.createButton(shortcuts) { onStop() }
        runButton = Actions.TOOL_RUN.createButton(shortcuts) { onRun() }

        val runStopStack = StackPane()
        runStopStack.children.addAll(stopButton, runButton)

        with(toolbar.getItems()) {
            add(optionsField)
            add(runStopStack)
            add(Actions.TOOL_SELECT.createToolButton(shortcuts) { tool -> onSelectTool(tool) })
            add(splitGroup)
        }

        autoUpdater = AutoUpdater("HalfTab") { updateButtons() }
    }

    fun updateButtons() {
        Platform.runLater {
            val tool = toolPane.tool

            val stoppable = tool is Stoppable
            val showStop = tool.toolRunner.isRunning() && stoppable
            stopButton.setVisible(showStop)
            runButton.setVisible(!showStop)
            runButton.setDisable(tool.toolRunner.isRunning())
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
}
