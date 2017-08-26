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

import javafx.css.Styleable
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
import uk.co.nickthecoder.paratask.SidePanel
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.CompoundButtons
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.util.RequestFocus
import uk.co.nickthecoder.paratask.util.Stoppable

class HalfTab_Impl(override var toolPane: ToolPane)

    : MySplitPane(), HalfTab {

    val mainArea = BorderPane()

    override val toolBars = BorderPane()

    private val toolBar = ToolBar()

    private val shortcuts = ShortcutHelper("HalfTab", this)

    override val optionsField = TextField()

    override lateinit var projectTab: ProjectTab

    val stopButton: Button

    val runButton: Button

    override val history = History(this)

    val optionsContextMenu = ContextMenu()

    var sidePanel: SidePanel? = null
        set(v) {
            if (right != null && left is Styleable) {
                (left as Styleable).styleClass.remove("sidebar")
            }
            if (v == null) {
                right = null
                left = mainArea
            } else {
                left = v.node
                right = mainArea
                if (v is Styleable) {
                    v.styleClass.add("sidebar")
                }
            }
            field = v
        }

    val sidePanelToggleButton = ParataskActions.SIDE_PANEL_TOGGLE.createButton(shortcuts) { toggleSidePanel() }

    init {
        this.dividerRatio = 0.3

        toolBars.center = toolBar

        left = mainArea

        with(mainArea) {
            center = toolPane as Node
            bottom = toolBars
        }

        with(optionsField) {
            prefColumnCount = 6
            addEventHandler(KeyEvent.KEY_PRESSED, { optionsFieldKeyPressed(it) })
            addEventHandler(MouseEvent.MOUSE_PRESSED, { optionFieldMouse(it) })
            addEventHandler(MouseEvent.MOUSE_RELEASED, { optionFieldMouse(it) })
            contextMenu = optionsContextMenu
        }

        val historyGroup = CompoundButtons()
        val backButton = ParataskActions.HISTORY_BACK.createButton(shortcuts) { history.undo() }
        val forwardButton = ParataskActions.HISTORY_FORWARD.createButton(shortcuts) { history.redo() }
        backButton.disableProperty().bind(history.canUndoProperty.not())
        forwardButton.disableProperty().bind(history.canRedoProperty.not())
        historyGroup.children.addAll(backButton, forwardButton)

        val runStopStack = StackPane()
        stopButton = ParataskActions.TOOL_STOP.createButton(shortcuts) { onStop() }
        runButton = ParataskActions.TOOL_RUN.createButton(shortcuts) { onRun() }
        runStopStack.children.addAll(stopButton, runButton)

        sidePanelToggleButton.isDisable = !toolPane.tool.hasSidePanel

        toolBar.items.addAll(
                optionsField,
                runStopStack,
                ParataskActions.TOOL_SELECT.createToolButton(shortcuts) { tool -> onSelectTool(tool) },
                historyGroup,
                sidePanelToggleButton,
                ParataskActions.TAB_SPLIT_TOGGLE.createButton(shortcuts) { projectTab.splitToggle() },
                ParataskActions.TAB_MERGE_TOGGLE.createButton(shortcuts) { projectTab.mergeToggle() },
                ParataskActions.TOOL_CLOSE.createButton(shortcuts) { close() })

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
        mainArea.center = null
        mainArea.bottom = null
        left = null
        right = null
        toolBars.children.clear()
        toolBar.items.clear()
        shortcuts.clear()
        optionsContextMenu.items.clear()
        ParaTaskApp.logAttach("HalfTab.detached ToolPane")
    }

    override fun isLeft() = projectTab.left === this


    fun bindButtons() {
        runButton.disableProperty().bind(toolPane.tool.taskRunner.disableRunProperty)
        runButton.visibleProperty().bind(toolPane.tool.taskRunner.showRunProperty)
        stopButton.visibleProperty().bind(toolPane.tool.taskRunner.showStopProperty)
    }

    override fun changeTool(tool: Tool, prompt: Boolean) {
        val showSidePanel = right != null

        sidePanel = null
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)

        mainArea.center = toolPane as Node
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
            toolPane.parametersPane.run()
        }

        sidePanelToggleButton.isDisable = !tool.hasSidePanel

        if (tool.hasSidePanel && showSidePanel) {
            toggleSidePanel()
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

    override fun close() {
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

        if (ParataskActions.OPTION_RUN.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = false, newTab = false)
        } else if (ParataskActions.OPTION_RUN_NEW_TAB.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = false, newTab = true)

        } else if (ParataskActions.OPTION_PROMPT.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = true, newTab = false)
        } else if (ParataskActions.OPTION_PROMPT_NEW_TAB.match(event)) {
            done = runner.runNonRow(optionsField.text, prompt = true, newTab = true)

        } else if (ParataskActions.CONTEXT_MENU.match(event)) {
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
        ParaTaskApp.logFocus("HalfTab_Impl focusOption. RequestFocus.requestFocus(optionsField)")
        RequestFocus.requestFocus(optionsField)
    }

    override fun focusOtherHalf() {
        val other = if (projectTab.left === this) projectTab.right else projectTab.left

        other?.toolPane?.focusResults()
    }

    fun toggleSidePanel() {
        if (sidePanel == null) {
            sidePanel = toolPane.tool.getSidePanel()
        } else {
            sidePanel = null
        }
    }

}
