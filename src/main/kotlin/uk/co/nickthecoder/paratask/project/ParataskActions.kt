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

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination

object ParataskActions {

    private val nameToActionMap = mutableMapOf<String, ParataskAction>()

    // ProjectWindow
    val OPEN_PROJECT = ParataskAction("project.open", KeyCode.O, alt = true, tooltip = "Open Project")
    val SAVE_PROJECT = ParataskAction("project.save", KeyCode.S, alt = true, tooltip = "Save Project")
    val QUIT = ParataskAction("application.quit", KeyCode.Q, alt = true, label = "Quit", tooltip = "Quit Para Task")
    val NEW_WINDOW = ParataskAction("window.new", KeyCode.N, control = true, tooltip = "New Window")
    val NEW_TAB = ParataskAction("tab.new", KeyCode.T, control = true, label = "New Tab", tooltip = "New Tab")
    val DUPLICATE_TAB = ParataskAction("tab.duplicate", KeyCode.D, control = true, tooltip = "Duplicate Tab")

    val SPLIT_TAB_TOGGLE = ParataskAction("split.tab.toggle", KeyCode.F3, tooltip = "Split/Un-Split")
    val APPLICATION_ABOUT = ParataskAction("application.about", KeyCode.F1, tooltip = "About ParaTask")

    // HalfTab
    val SPLIT_TOOL_TOGGLE = ParataskAction("tool.toggleParameters", KeyCode.F9, tooltip = "Show/Hide Parameters")

    val TOOL_STOP = ParataskAction("tool.stop", KeyCode.ESCAPE, shift = true, tooltip = "Stop the Tool")
    val TOOL_RUN = ParataskAction("tool.run", KeyCode.F5, tooltip = "(Re) Run the Tool")

    val TOOL_SELECT = ParataskAction("tool.select", KeyCode.HOME, control = true, tooltip = "Select a Tool")
    val TOOL_CLOSE = ParataskAction("tool.close", KeyCode.W, control = true, tooltip = "Close Tool")

    val HISTORY_BACK = ParataskAction("history.back", KeyCode.LEFT, alt = true, tooltip = "Back")
    val HISTORY_FORWARD = ParataskAction("history.forward", KeyCode.RIGHT, alt = true, tooltip = "Forward")

    // AbstractTableResults
    val OPTION_RUN = ParataskAction("actions.run", KeyCode.ENTER)
    val OPTION_RUN_NEW_TAB = ParataskAction("actions.run.new.tab", KeyCode.ENTER, shift = true)
    val OPTION_PROMPT = ParataskAction("actions.run", KeyCode.F8)
    val OPTION_PROMPT_NEW_TAB = ParataskAction("actions.run.new.tab", KeyCode.F8, shift = true)

    // FileField
    val UP_DIRECTORY = ParataskAction("directory.up", KeyCode.UP, alt = true)
    val COMPLETE_FILE = ParataskAction("file.complete", KeyCode.DOWN, alt = true)

    // EditorTool

    val EDIT_FIND = ParataskAction("edit.find", KeyCode.F, control = true, tooltip = "Find")
    val EDIT_FIND_GO = ParataskAction("edit.find.go", KeyCode.ENTER, label = "Find")
    val EDIT_FIND_NEXT = ParataskAction("edit.find.next", KeyCode.G, control = true, tooltip = "Find Next")
    val EDIT_FIND_PREV = ParataskAction("edit.find.prev", KeyCode.G, control = true, shift = true, tooltip = "Find Next")

    // General
    val CONTEXT_MENU = ParataskAction("context.menu", KeyCode.CONTEXT_MENU)
    val FILE_SAVE = ParataskAction("file.save", KeyCode.S, control = true, tooltip = "Save")
    val EDIT_CUT = ParataskAction("edit.cut", KeyCode.X, control = true, tooltip = "Cut")
    val EDIT_COPY = ParataskAction("edit.copy", KeyCode.C, control = true, tooltip = "Copy")
    val EDIT_PASTE = ParataskAction("edit.paste", KeyCode.V, control = true, tooltip = "Paste")
    val EDIT_UNDO = ParataskAction("edit.undo", KeyCode.Z, control = true, tooltip = "Undo")
    val EDIT_REDO = ParataskAction("edit.redo", KeyCode.Z, shift = true, control = true, tooltip = "Redo")
    val ESCAPE = ParataskAction("escape", KeyCode.ESCAPE)

    // GlobalShortcuts
    val NEXT_MAJOR_TAB = ParataskAction("tab.major.next", KeyCode.CLOSE_BRACKET, control = true)
    val PREV_MAJOR_TAB = ParataskAction("tab.major.prev", KeyCode.OPEN_BRACKET, control = true)
    val NEXT_MINOR_TAB = ParataskAction("tab.minor.next", KeyCode.CLOSE_BRACKET, control = true, shift = true)
    val PREV_MINOR_TAB = ParataskAction("tab.minor.prev", KeyCode.OPEN_BRACKET, control = true, shift = true)

    val FOCUS_OPTION = ParataskAction("focus.option", KeyCode.F10)
    val FOCUS_RESULTS = ParataskAction("focus.results", KeyCode.F10, control = true)
    val FOCUS_HEADER = ParataskAction("focus.header", KeyCode.F10, shift = true)
    val FOCUS_OTHER_SPLIT = ParataskAction("focus.other.split", KeyCode.F3, control = true)

    val MAJOR_TAB_1 = ParataskAction("tab.major.1", KeyCode.DIGIT1, control = true)
    val MAJOR_TAB_2 = ParataskAction("tab.major.2", KeyCode.DIGIT2, control = true)
    val MAJOR_TAB_3 = ParataskAction("tab.major.3", KeyCode.DIGIT3, control = true)
    val MAJOR_TAB_4 = ParataskAction("tab.major.4", KeyCode.DIGIT4, control = true)
    val MAJOR_TAB_5 = ParataskAction("tab.major.5", KeyCode.DIGIT5, control = true)
    val MAJOR_TAB_6 = ParataskAction("tab.major.6", KeyCode.DIGIT6, control = true)
    val MAJOR_TAB_7 = ParataskAction("tab.major.7", KeyCode.DIGIT7, control = true)
    val MAJOR_TAB_8 = ParataskAction("tab.major.8", KeyCode.DIGIT8, control = true)
    val MAJOR_TAB_9 = ParataskAction("tab.major.9", KeyCode.DIGIT9, control = true)

    val MAJOR_TABS = listOf(MAJOR_TAB_1, MAJOR_TAB_2, MAJOR_TAB_3, MAJOR_TAB_4, MAJOR_TAB_5, MAJOR_TAB_6, MAJOR_TAB_7, MAJOR_TAB_8, MAJOR_TAB_9)

    val MINOR_TAB_1 = ParataskAction("tab.minor.1", KeyCode.DIGIT1, control = true, shift = true)
    val MINOR_TAB_2 = ParataskAction("tab.minor.2", KeyCode.DIGIT2, control = true, shift = true)
    val MINOR_TAB_3 = ParataskAction("tab.minor.3", KeyCode.DIGIT3, control = true, shift = true)
    val MINOR_TAB_4 = ParataskAction("tab.minor.4", KeyCode.DIGIT4, control = true, shift = true)
    val MINOR_TAB_5 = ParataskAction("tab.minor.5", KeyCode.DIGIT5, control = true, shift = true)
    val MINOR_TAB_6 = ParataskAction("tab.minor.6", KeyCode.DIGIT6, control = true, shift = true)
    val MINOR_TAB_7 = ParataskAction("tab.minor.7", KeyCode.DIGIT7, control = true, shift = true)
    val MINOR_TAB_8 = ParataskAction("tab.minor.8", KeyCode.DIGIT8, control = true, shift = true)
    val MINOR_TAB_9 = ParataskAction("tab.minor.9", KeyCode.DIGIT9, control = true, shift = true)

    val MINOR_TABS = listOf(MINOR_TAB_1, MINOR_TAB_2, MINOR_TAB_3, MINOR_TAB_4, MINOR_TAB_5, MINOR_TAB_6, MINOR_TAB_7, MINOR_TAB_8, MINOR_TAB_9)


    val FOCUS_NEXT = ParataskAction("focus.next", KeyCode.TAB)
    val INSERT_TAB = ParataskAction("insert.tab", KeyCode.TAB, control = true)

    val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
    val acceleratorUp = KeyCodeCombination(KeyCode.UP)
    val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)


    fun add(action: ParataskAction) {
        nameToActionMap.put(action.name, action)
    }
}
