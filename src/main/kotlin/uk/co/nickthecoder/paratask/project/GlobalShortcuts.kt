package uk.co.nickthecoder.paratask.project

import javafx.scene.Node
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.tools.HomeTool

class GlobalShortcuts(val scene: Scene, val projectWindow: ProjectWindow) {

    init {
        put(Actions.SPLIT_TAB_TOGGLE) { toolPane()?.toggleParameters() }
        put(Actions.TOOL_STOP) { halfTab()?.onStop() }
        put(Actions.TOOL_RUN) { halfTab()?.onRun() }
        put(Actions.TOOL_SELECT) { projectWindow.addTool(HomeTool()) }
        put(Actions.TOOL_CLOSE) { halfTab()?.onClose() }
        put(Actions.HISTORY_BACK) { halfTab()?.history?.undo() }
        put(Actions.HISTORY_FORWARD) { halfTab()?.history?.redo() }

        put(Actions.NEXT_MAJOR_TAB) { projectWindow.tabs.nextTab() }
        put(Actions.PREV_MAJOR_TAB) { projectWindow.tabs.prevTab() }
        put(Actions.NEXT_MINOR_TAB) { toolPane()?.nextTab() }
        put(Actions.PREV_MINOR_TAB) { toolPane()?.prevTab() }

        put(Actions.FOCUS_HEADER) { toolPane()?.focusHeader() }
        put(Actions.FOCUS_RESULTS) { toolPane()?.focusResults() }
        put(Actions.FOCUS_OPTION) { halfTab()?.focusOption() }

        put(Actions.FOCUS_OTHER_SPLIT) { halfTab()?.focusOtherHalf() }

        for ((index, action) in Actions.MAJOR_TABS.withIndex()) {
            put(action) { projectWindow.tabs.selectTab(index) }
        }

        for ((index, action) in Actions.MINOR_TABS.withIndex()) {
            put(action) { toolPane()?.selectTab(index) }
        }

    }

    fun put(action: Action, call: () -> Unit) {
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