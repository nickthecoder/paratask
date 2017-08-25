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

import javafx.scene.Node
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.tools.HomeTool

class GlobalShortcuts(val scene: Scene, val projectWindow: ProjectWindow) {

    init {
        put(ParataskActions.TOOL_STOP) { halfTab()?.onStop() }
        put(ParataskActions.TOOL_RUN) { halfTab()?.onRun() }
        put(ParataskActions.TOOL_SELECT) { projectWindow.addTool(HomeTool()) }
        put(ParataskActions.TOOL_CLOSE) { halfTab()?.onClose() }
        put(ParataskActions.HISTORY_BACK) { halfTab()?.history?.undo() }
        put(ParataskActions.HISTORY_FORWARD) { halfTab()?.history?.redo() }

        put(ParataskActions.NEXT_MAJOR_TAB) { projectWindow.tabs.nextTab() }
        put(ParataskActions.PREV_MAJOR_TAB) { projectWindow.tabs.prevTab() }
        put(ParataskActions.NEXT_MINOR_TAB) { toolPane()?.nextTab() }
        put(ParataskActions.PREV_MINOR_TAB) { toolPane()?.prevTab() }

        put(ParataskActions.FOCUS_HEADER) {
            ParaTaskApp.logFocus("GlobalShortcuts FOCUS_HEADER. toolPane().focusHeader()")
            toolPane()?.focusHeader()
        }
        put(ParataskActions.FOCUS_RESULTS) {
            ParaTaskApp.logFocus("GlobalShortcuts FOCUS_RESULTS. toolPane().focusResults()")
            toolPane()?.focusResults()
        }
        put(ParataskActions.FOCUS_OPTION) {
            ParaTaskApp.logFocus("GlobalShortcuts FOCUS_OPTION. halfTab().focusOption()")
            halfTab()?.focusOption()
        }

        put(ParataskActions.FOCUS_OTHER_SPLIT) {
            ParaTaskApp.logFocus("GlobalShortcuts FOCUS_OTHER_SPLIT. halfTab().focusOtherHalf()")
            halfTab()?.focusOtherHalf()
        }

        put(ParataskActions.TAB_NEW) { projectWindow.tabs.addTool(HomeTool()) }
        put(ParataskActions.TAB_RESTORE) { projectWindow.tabs.restoreTab() }

        for ((index, action) in ParataskActions.MAJOR_TABS.withIndex()) {
            put(action) { projectWindow.tabs.selectTab(index) }
        }

        for ((index, action) in ParataskActions.MINOR_TABS.withIndex()) {
            put(action) { toolPane()?.selectTab(index) }
        }

    }

    fun put(action: ParataskAction, call: () -> Unit) {
        scene.accelerators.put(action.keyCodeCombination, Runnable { call() })
    }

    fun tab(): ProjectTab? = projectWindow.tabs.currentTab()

    fun halfTab(): HalfTab? {
        var node: Node? = scene.focusOwner
        while (node != null) {
            if (node is HalfTab) {
                return node
            }
            node = node.parent
        }

        return tab()?.left
    }

    fun toolPane(): ToolPane? = halfTab()?.toolPane


}