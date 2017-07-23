/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.project

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.CompoundButtons
import uk.co.nickthecoder.paratask.util.RequestFocus
import uk.co.nickthecoder.paratask.util.Stoppable

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab {

    override val toolBars = BorderPane()

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = TextField()

    override lateinit var projectTab: ProjectTab

    val stopButton: Button

    val runButton: Button

    override val history = History(this)

    val optionsContextMenu = ContextMenu()

    init {
        toolBars.center = toolBar

        center = toolPane as Node
        bottom = toolBars

        with(optionsField) {
            prefColumnCount = 6
            addEventHandler(KeyEvent.KEY_PRESSED, { optionsFieldKeyPressed(it) })
            addEventHandler(MouseEvent.MOUSE_PRESSED, { optionFieldMouse(it) })
            addEventHandler(MouseEvent.MOUSE_RELEASED, { optionFieldMouse(it) })
            contextMenu = optionsContextMenu
        }

        val historyGroup = CompoundButtons()
        val backButton = Actions.HISTORY_BACK.createButton(shortcuts) { history.undo() }
        val forwardButton = Actions.HISTORY_FORWARD.createButton(shortcuts) { history.redo() }
        backButton.disableProperty().bind(history.canUndoProperty.not())
        forwardButton.disableProperty().bind(history.canRedoProperty.not())
        historyGroup.children.addAll(backButton, forwardButton)

        val runStopStack = StackPane()
        stopButton = Actions.TOOL_STOP.createButton(shortcuts) { onStop() }
        runButton = Actions.TOOL_RUN.createButton(shortcuts) { onRun() }
        runStopStack.children.addAll(stopButton, runButton)

        with(toolBar.items) {
            add(optionsField)
            add(runStopStack)
            add(Actions.TOOL_SELECT.createToolButton(shortcuts) { tool -> onSelectTool(tool) })
            add(Actions.SPLIT_TOOL_TOGGLE.createButton(shortcuts) { toolPane.toggleParameters() })
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

    override fun isLeft() = projectTab.left === this


    fun bindButtons() {
        runButton.disableProperty().bind(toolPane.tool.taskRunner.disableRunProperty)
        runButton.visibleProperty().bind(toolPane.tool.taskRunner.showRunProperty)
        stopButton.visibleProperty().bind(toolPane.tool.taskRunner.showStopProperty)
    }

    override fun changeTool(tool: Tool, prompt: Boolean) {
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)

        center = toolPane as Node
        toolPane.attached(this)

        projectTab.changed()

        bindButtons()
        history.push(tool)

        if (!prompt) {
            try {
                tool.check()
            } catch(e: Exception) {
                return
            }
            tool.taskRunner.run()
        }
    }

    override fun onStop() {
        val tool = toolPane.tool
        if (tool is Stoppable) {
            tool.stop()
        }
    }

    override fun onRun() {
        toolPane.parametersPane.run()
    }

    fun onSelectTool(tool: Tool) {
        val newTool = tool.copy()
        newTool.resolveParameters(projectTab.projectTabs.projectWindow.project.resolver)
        changeTool(newTool)
    }

    override fun onClose() {
        projectTab.remove(toolPane)
    }

    override fun pushHistory() {
        history.push(toolPane.tool)
    }

    override fun pushHistory(tool: Tool) {
        history.push(tool)
    }

    fun optionsFieldKeyPressed(event: KeyEvent) {
        var done = false
        val tool = toolPane.resultsTool()
        val runner = tool.optionsRunner

        if (Actions.OPTION_RUN.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = false, newTab = false)
        } else if (Actions.OPTION_RUN_NEW_TAB.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = false, newTab = true)

        } else if (Actions.OPTION_PROMPT.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = true, newTab = false)
        } else if (Actions.OPTION_PROMPT_NEW_TAB.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = true, newTab = true)

        } else if (Actions.CONTEXT_MENU.match(event)) {
            onOptionsContextMenu()
            event.consume()
        }

        if (done) {
            optionsField.text = ""
        }
    }

    fun optionFieldMouse(event: MouseEvent) {
        if (event.isPopupTrigger) {
            onOptionsContextMenu()
            event.consume()
        }
    }

    private fun onOptionsContextMenu() {
        val tool = toolPane.resultsTool()
        tool.optionsRunner.createNonRowOptionsMenu(optionsContextMenu)
        optionsContextMenu.show(optionsField, Side.BOTTOM, 0.0, 0.0)
    }

    override fun focusOption() {
        RequestFocus.requestFocus(optionsField)
    }

    override fun focusOtherHalf() {
        val other = if (projectTab.left === this) projectTab.right else projectTab.left

        other?.toolPane?.focusResults()
    }
}
