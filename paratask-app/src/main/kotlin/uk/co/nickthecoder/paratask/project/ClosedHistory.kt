package uk.co.nickthecoder.paratask.project

import javafx.scene.input.KeyCodeCombination
import uk.co.nickthecoder.paratask.Tool

/**
 * Remembers tabs that have been closed, so that they can be opened again.
 */
class ClosedHistory {

    val items = mutableListOf<ClosedTab>()

    fun remember(projectTab: ProjectTab) {
        val closedTab = ClosedTab(projectTab)
        items.add(closedTab)
    }

    fun canRestore(): Boolean {
        return items.isNotEmpty()
    }

    fun restore(projectTabs: ProjectTabs) {
        val closedTab = items.removeAt(items.size - 1)
        closedTab.createTab(projectTabs)
    }
}

class ClosedTab(projectTab: ProjectTab) {

    val leftHistory: Pair<List<History.Moment>, Int>

    val rightHistory: Pair<List<History.Moment>, Int>?

    val tabTemplate: String

    val tabShortcut: KeyCodeCombination?

    // The index of the tab. 0 for the first tab etc.
    val index: Int

    init {
        projectTab.left.history.push(projectTab.left.toolPane.tool)
        projectTab.right?.history?.push(projectTab.right?.toolPane?.tool!!)

        leftHistory = projectTab.left.history.save()
        rightHistory = projectTab.right?.history?.save()

        tabTemplate = projectTab.tabTemplate
        tabShortcut = projectTab.tabShortcut

        index = projectTab.projectTabs.indexOf(projectTab)
    }

    fun createTab(projectTabs: ProjectTabs): ProjectTab {
        val leftTool = createTool(leftHistory)
        val tab = projectTabs.addTool(index, leftTool)
        tab.tabTemplate = tabTemplate
        tab.tabShortcut = tabShortcut

        leftTool.toolPane?.halfTab?.history?.restore(leftHistory.first, leftHistory.second)

        rightHistory?.let {
            val rightTool = createTool(it)
            tab.split(rightTool)
            rightTool.toolPane?.halfTab?.history?.restore(rightHistory.first, rightHistory.second)
        }

        return tab
    }

    private fun createTool(history: Pair<List<History.Moment>, Int>): Tool {
        val moment = history.first[history.second]
        return moment.tool
    }

}
