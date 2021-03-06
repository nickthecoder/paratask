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

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import uk.co.nickthecoder.paratask.util.child
import java.io.*

object ParataskActions {

    val nameToActionMap = mutableMapOf<String, ParataskAction>()

    // ProjectWindow

    val PROJECT_OPEN = ParataskAction("project.open", KeyCode.O, alt = true, tooltip = "Open Project")
    val PROJECT_SAVE = ParataskAction("project.save", KeyCode.S, alt = true, tooltip = "Save Project")
    val QUIT = ParataskAction("application.quit", KeyCode.Q, alt = true, label = "Quit", tooltip = "Quit Para Task")
    val WINDOW_NEW = ParataskAction("window.new", KeyCode.N, control = true, tooltip = "New Window")
    val TAB_NEW = ParataskAction("tab.new", KeyCode.T, control = true, label = "New Tab")
    val TAB_RESTORE = ParataskAction("tab.restore", KeyCode.T, shift = true, control = true, label = "Restore Tab")
    val APPLICATION_ABOUT = ParataskAction("application.about", KeyCode.F1, tooltip = "About ParaTask")

    // ProjectTab

    val TAB_PROPERTIES = ParataskAction("tab.properties", null, label = "Properties")
    val TAB_DUPLICATE = ParataskAction("tab.duplicate", KeyCode.D, alt = true, label = "Duplicate Tab", tooltip = "Duplicate Tab")
    val TAB_CLOSE = ParataskAction("tab.close", KeyCode.W, control = true, label = "Close Tab")

    // HalfTab

    val RESULTS_TAB_CLOSE = ParataskAction("results-tab.close", KeyCode.W, alt = true, label = "Close Results Tab")

    val TOOL_STOP = ParataskAction("tool.stop", KeyCode.ESCAPE, shift = true, tooltip = "Stop the Tool")
    val TOOL_RUN = ParataskAction("tool.run", KeyCode.F5, tooltip = "(Re) Run the Tool")

    val TOOL_SELECT = ParataskAction("tool.select", KeyCode.HOME, control = true, tooltip = "Select a Tool")
    val TOOL_CLOSE = ParataskAction("tool.close", KeyCode.W, control = true, shift = true, tooltip = "Close Tool")

    val HISTORY_BACK = ParataskAction("history.back", KeyCode.LEFT, alt = true, tooltip = "Back")
    val HISTORY_FORWARD = ParataskAction("history.forward", KeyCode.RIGHT, alt = true, tooltip = "Forward")

    val TAB_MERGE_TOGGLE = ParataskAction("tab.merge.toggle", null, tooltip = "Split / Merge with the Tab to the right")
    val TAB_SPLIT_TOGGLE = ParataskAction("tab.split.toggle", KeyCode.F3, tooltip = "Split/Un-Split")

    val SIDE_PANEL_TOGGLE = ParataskAction("side.panel.toggle", KeyCode.F9, tooltip = "Show/Hide the side panel")

    val OPTION_RUN = ParataskAction("actions.run", KeyCode.ENTER)
    val OPTION_RUN_NEW_TAB = ParataskAction("actions.run.new.tab", KeyCode.ENTER, shift = true)
    val OPTION_PROMPT = ParataskAction("actions.run", KeyCode.F8)
    val OPTION_PROMPT_NEW_TAB = ParataskAction("actions.run.new.tab", KeyCode.F8, shift = true)

    val PARAMETERS_SHOW = ParataskAction("parameters.show", KeyCode.P, control = true, label = "Show Parameters")
    val RESULTS_SHOW = ParataskAction("results.show", KeyCode.R, control = true, label = "Show Results")

    // Table

    val NEXT_ROW = ParataskAction("row.next", KeyCode.DOWN)
    val PREV_ROW = ParataskAction("row.previous", KeyCode.UP)
    val SELECT_ROW_DOWN = ParataskAction("row.select.down", KeyCode.DOWN, shift = true)
    val SELECT_ROW_UP = ParataskAction("row.select.up", KeyCode.UP, shift = true)

    // DirectoryTool

    val DIRECTORY_EDIT_PLACES = ParataskAction("directory.places.edit", null, label = "Edit", tooltip = "Edit Places")
    val DIRECTORY_CHANGE_PLACES = ParataskAction("directory.places.change", null, label = "Change Places File")
    val DIRECTORY_CHANGE_TREE_ROOT = ParataskAction("director.change.root", null, label = "Change Root", tooltip = "Change tree's root directory")

    // EditorTool

    val EDIT_FIND = ParataskAction("edit.find", KeyCode.F, control = true, tooltip = "Find")
    val EDIT_REPLACE = ParataskAction("edit.replace", KeyCode.H, control = true, tooltip = "Replace")

    // General

    val CONTEXT_MENU = ParataskAction("context.menu", KeyCode.CONTEXT_MENU)
    val FILE_SAVE = ParataskAction("file.save", KeyCode.S, control = true, tooltip = "Save")
    val EDIT_CUT = ParataskAction("edit.cut", KeyCode.X, control = true, tooltip = "Cut")
    val EDIT_COPY = ParataskAction("edit.copy", KeyCode.C, control = true, tooltip = "Copy")
    val EDIT_PASTE = ParataskAction("edit.paste", KeyCode.V, control = true, tooltip = "Paste")
    val EDIT_UNDO = ParataskAction("edit.undo", KeyCode.Z, control = true, tooltip = "Undo")
    val EDIT_REDO = ParataskAction("edit.redo", KeyCode.Z, shift = true, control = true, tooltip = "Redo")
    val ESCAPE = ParataskAction("escape", KeyCode.ESCAPE)
    val SELECT_ALL = ParataskAction("select-all", KeyCode.A, control = true)
    val SELECT_NONE = ParataskAction("select-all", KeyCode.A, control = true, shift = true)

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

    init {
        load()
    }

    fun add(action: ParataskAction) {
        nameToActionMap.put(action.name, action)
    }

    fun shortcutPrefencesFile() = Preferences.configDirectory.child("shortcuts.json")

    fun save() {

        val jroot = JsonObject()
        val jshortcuts = JsonArray()

        nameToActionMap.values.forEach { action ->
            if (action.isChanged()) {
                val jshortcut = JsonObject()

                jshortcut.add("name", action.name)
                jshortcut.add("keycode", action.keyCodeCombination?.code?.toString() ?: "")
                addModifier(jshortcut, "control", action.keyCodeCombination?.control)
                addModifier(jshortcut, "shift", action.keyCodeCombination?.shift)
                addModifier(jshortcut, "alt", action.keyCodeCombination?.alt)

                jshortcuts.add(jshortcut)
            }
        }
        jroot.add("shortcuts", jshortcuts)

        BufferedWriter(OutputStreamWriter(FileOutputStream(shortcutPrefencesFile()))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }
    }

    fun addModifier(jparent: JsonObject, name: String, mod: KeyCombination.ModifierValue?) {
        mod?.let { jparent.add(name, mod.toString()) }
    }

    fun load() {

        val file = shortcutPrefencesFile()
        if (!file.exists()) {
            return
        }

        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()
        val jshortcutsObj = jroot.get("shortcuts")
        jshortcutsObj?.let {
            val jshortcuts = it.asArray()
            jshortcuts.forEach {
                val jshortcut = it.asObject()
                val name = jshortcut.getString("name", "")
                val action = nameToActionMap[name]
                action?.let {
                    val keyCodeS = jshortcut.getString("keycode", "")
                    val controlS = jshortcut.getString("control", "ANY")
                    val shiftS = jshortcut.getString("shift", "ANY")
                    val altS = jshortcut.getString("alt", "ANY")

                    val control = KeyCombination.ModifierValue.valueOf(controlS)
                    val shift = KeyCombination.ModifierValue.valueOf(shiftS)
                    val alt = KeyCombination.ModifierValue.valueOf(altS)

                    if (keyCodeS == "") {
                        it.keyCodeCombination = null
                    } else {
                        val keyCode = KeyCode.valueOf(keyCodeS)
                        it.keyCodeCombination = KeyCodeCombination(
                                keyCode,
                                control,
                                shift,
                                alt,
                                KeyCombination.ModifierValue.UP,
                                KeyCombination.ModifierValue.UP)
                    }
                }
            }
        }
    }

}
