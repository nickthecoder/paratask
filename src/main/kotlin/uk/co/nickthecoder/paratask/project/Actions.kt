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

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.tools.HomeTool

object Actions {

    private val nameToActionMap = mutableMapOf<String, Action>()

    // ProjectWindow
    val OPEN_PROJECT = Action("project.open", KeyCode.O, alt = true, tooltip = "Open Project")
    val SAVE_PROJECT = Action("project.save", KeyCode.S, alt = true, tooltip = "Save Project")
    val QUIT = Action("application.quit", KeyCode.Q, alt = true, label = "Quit", tooltip = "Quit Para Task")
    val NEW_WINDOW = Action("window.new", KeyCode.N, control = true, tooltip = "New Window")
    val NEW_TAB = Action("tab.new", KeyCode.T, control = true, label = "New Tab", tooltip = "New Tab")
    val DUPLICATE_TAB = Action("tab.duplicate", KeyCode.D, control = true, tooltip = "Duplicate Tab")

    val SPLIT_TAB_TOGGLE = Action("split.tab.toggle", KeyCode.F3, tooltip = "Split/Un-Split")
    val APPLICATION_ABOUT = Action("application.about", KeyCode.F1, tooltip = "About ParaTask")

    // HalfTab
    val SPLIT_TOOL_TOGGLE = Action("tool.toggleParameters", KeyCode.F9, tooltip = "Show/Hide Parameters")

    val TOOL_STOP = Action("tool.stop", KeyCode.ESCAPE, shift = true, tooltip = "Stop the Tool")
    val TOOL_RUN = Action("tool.run", KeyCode.F5, tooltip = "(Re) Run the Tool")

    val TOOL_SELECT = Action("tool.select", KeyCode.HOME, control = true, tooltip = "Select a Tool")
    val TOOL_CLOSE = Action("tool.close", KeyCode.W, control = true, tooltip = "Close Tool")

    val HISTORY_BACK = Action("history.back", KeyCode.LEFT, alt = true, tooltip = "Back")
    val HISTORY_FORWARD = Action("history.forward", KeyCode.RIGHT, alt = true, tooltip = "Forward")

    // AbstractTableResults
    val OPTION_RUN = Action("actions.run", KeyCode.ENTER)
    val OPTION_RUN_NEW_TAB = Action("actions.run.new.tab", KeyCode.ENTER, shift = true)
    val OPTION_PROMPT = Action("actions.run", KeyCode.F8)
    val OPTION_PROMPT_NEW_TAB = Action("actions.run.new.tab", KeyCode.F8, shift = true)

    // FileField
    val UP_DIRECTORY = Action("directory.up", KeyCode.UP, alt = true)
    val COMPLETE_FILE = Action("file.complete", KeyCode.DOWN, alt = true)

    // EditorTool

    val EDIT_FIND = Action("edit.find", KeyCode.F, control = true, tooltip = "Find")
    val EDIT_FIND_GO = Action("edit.find.go", KeyCode.ENTER, label = "Find")
    val EDIT_FIND_NEXT = Action("edit.find.next", KeyCode.G, control = true, tooltip = "Find Next")
    val EDIT_FIND_PREV = Action("edit.find.prev", KeyCode.G, control = true, shift = true, tooltip = "Find Next")

    // General
    val CONTEXT_MENU = Action("context.menu", KeyCode.CONTEXT_MENU)
    val FILE_SAVE = Action("file.save", KeyCode.S, control = true, tooltip = "Save")
    val EDIT_CUT = Action("edit.cut", KeyCode.X, control = true, tooltip = "Cut")
    val EDIT_COPY = Action("edit.copy", KeyCode.C, control = true, tooltip = "Copy")
    val EDIT_PASTE = Action("edit.paste", KeyCode.V, control = true, tooltip = "Paste")
    val EDIT_UNDO = Action("edit.undo", KeyCode.Z, control = true, tooltip = "Undo")
    val EDIT_REDO = Action("edit.redo", KeyCode.Z, shift = true, control = true, tooltip = "Redo")
    val ESCAPE = Action("escape", KeyCode.ESCAPE)

    // GlobalShortcuts
    val NEXT_MAJOR_TAB = Action("tab.major.next", KeyCode.CLOSE_BRACKET, control = true)
    val PREV_MAJOR_TAB = Action("tab.major.prev", KeyCode.OPEN_BRACKET, control = true)
    val NEXT_MINOR_TAB = Action("tab.minor.next", KeyCode.CLOSE_BRACKET, control = true, shift = true)
    val PREV_MINOR_TAB = Action("tab.minor.prev", KeyCode.OPEN_BRACKET, control = true, shift = true)

    val FOCUS_OPTION = Action("focus.option", KeyCode.F10)
    val FOCUS_RESULTS = Action("focus.results", KeyCode.F10, control = true)
    val FOCUS_HEADER = Action("focus.header", KeyCode.F10, shift = true)
    val FOCUS_OTHER_SPLIT = Action("focus.other.split", KeyCode.F3, control = true)

    val MAJOR_TAB_1 = Action("tab.major.1", KeyCode.DIGIT1, control = true)
    val MAJOR_TAB_2 = Action("tab.major.2", KeyCode.DIGIT2, control = true)
    val MAJOR_TAB_3 = Action("tab.major.3", KeyCode.DIGIT3, control = true)
    val MAJOR_TAB_4 = Action("tab.major.4", KeyCode.DIGIT4, control = true)
    val MAJOR_TAB_5 = Action("tab.major.5", KeyCode.DIGIT5, control = true)
    val MAJOR_TAB_6 = Action("tab.major.6", KeyCode.DIGIT6, control = true)
    val MAJOR_TAB_7 = Action("tab.major.7", KeyCode.DIGIT7, control = true)
    val MAJOR_TAB_8 = Action("tab.major.8", KeyCode.DIGIT8, control = true)
    val MAJOR_TAB_9 = Action("tab.major.9", KeyCode.DIGIT9, control = true)

    val MAJOR_TABS = listOf(MAJOR_TAB_1, MAJOR_TAB_2, MAJOR_TAB_3, MAJOR_TAB_4, MAJOR_TAB_5, MAJOR_TAB_6, MAJOR_TAB_7, MAJOR_TAB_8, MAJOR_TAB_9)

    val MINOR_TAB_1 = Action("tab.minor.1", KeyCode.DIGIT1, control = true, shift = true)
    val MINOR_TAB_2 = Action("tab.minor.2", KeyCode.DIGIT2, control = true, shift = true)
    val MINOR_TAB_3 = Action("tab.minor.3", KeyCode.DIGIT3, control = true, shift = true)
    val MINOR_TAB_4 = Action("tab.minor.4", KeyCode.DIGIT4, control = true, shift = true)
    val MINOR_TAB_5 = Action("tab.minor.5", KeyCode.DIGIT5, control = true, shift = true)
    val MINOR_TAB_6 = Action("tab.minor.6", KeyCode.DIGIT6, control = true, shift = true)
    val MINOR_TAB_7 = Action("tab.minor.7", KeyCode.DIGIT7, control = true, shift = true)
    val MINOR_TAB_8 = Action("tab.minor.8", KeyCode.DIGIT8, control = true, shift = true)
    val MINOR_TAB_9 = Action("tab.minor.9", KeyCode.DIGIT9, control = true, shift = true)

    val MINOR_TABS = listOf(MINOR_TAB_1, MINOR_TAB_2, MINOR_TAB_3, MINOR_TAB_4, MINOR_TAB_5, MINOR_TAB_6, MINOR_TAB_7, MINOR_TAB_8, MINOR_TAB_9)


    val FOCUS_NEXT = Action("focus.next", KeyCode.TAB)
    val INSERT_TAB = Action("insert.tab", KeyCode.TAB, control = true)

    val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
    val acceleratorUp = KeyCodeCombination(KeyCode.UP)
    val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)


    fun add(action: Action) {
        nameToActionMap.put(action.name, action)
    }
}

private fun modifier(down: Boolean?) =
        if (down == null) {
            KeyCombination.ModifierValue.ANY
        } else if (down) {
            KeyCombination.ModifierValue.DOWN
        } else {
            KeyCombination.ModifierValue.UP
        }

fun createKeyCodeCombination(
        keyCode: KeyCode,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false): KeyCodeCombination {

    return KeyCodeCombination(
            keyCode,
            modifier(shift), modifier(control), modifier(alt), modifier(meta), modifier(shortcut))
}

class Action(
        val name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        val tooltip: String? = null,
        val label: String? = null
) {

    val keyCodeCombination: KeyCodeCombination? = if (keyCode == null) {
        null
    } else {
        createKeyCodeCombination(keyCode, shift, control, alt, meta, shortcut)
    }

    val image: Image? = ParaTaskApp.imageResource("buttons/$name.png")

    init {

        Actions.add(this)
    }

    fun match(event: KeyEvent): Boolean {
        return keyCodeCombination?.match(event) == true
    }

    fun createTooltip(): Tooltip? {
        if (tooltip == null && keyCodeCombination == null) {
            return null
        }

        val result = StringBuilder()
        tooltip?.let { result.append(it) }

        if (tooltip != null && keyCodeCombination != null) {
            result.append(" ")
        }
        keyCodeCombination?.let { result.append(it.displayText) }

        return Tooltip(result.toString())
    }

    fun createButton(shortcuts: ShortcutHelper? = null, action: () -> Unit): Button {

        shortcuts?.add(this, action)

        val button = Button()
        if (image == null) {
            button.text = label ?: name
        } else {
            button.graphic = ImageView(image)
        }
        if (label != null) {
            button.text = label
        }
        button.onAction = EventHandler {
            action()
        }
        button.tooltip = createTooltip()
        return button
    }

    fun createToolButton(shortcuts: ShortcutHelper? = null, action: (Tool) -> Unit): ToolSplitMenuButton {
        shortcuts?.let { it.add(this) { action(HomeTool()) } }

        val split = ToolSplitMenuButton(label ?: "", image, action)
        split.tooltip = createTooltip()

        return split
    }

    class ToolSplitMenuButton(label: String, icon: Image?, val action: (Tool) -> Unit)
        : SplitMenuButton() {

        init {
            text = label
            graphic = ImageView(icon)
            onAction = EventHandler { action(HomeTool()) }
        }

        init {

            TaskRegistry.home.listTools().forEach { tool ->
                val imageView = tool.icon?.let { ImageView(it) }
                val item = MenuItem(tool.shortTitle, imageView)

                item.onAction = EventHandler {
                    action(tool)
                }
                items.add(item)
            }

        }
    }

}
