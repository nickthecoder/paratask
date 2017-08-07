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

package uk.co.nickthecoder.paratask.options

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

open class OptionsRunner(val tool: Tool) {

    protected var refresher: Refresher = Refresher()

    fun createNonRowOptionsMenu(contextMenu: ContextMenu) {

        contextMenu.items.clear()

        val optionsName = tool.optionsName
        val topLevelOptions = OptionsManager.getTopLevelOptions(optionsName)

        var needSep = false

        for (fileOptions in topLevelOptions.listFileOptions()) {
            var added = false
            for (option in fileOptions.listOptions()) {
                if (!option.isRow) {
                    if (needSep) {
                        needSep = false
                        contextMenu.items.add(SeparatorMenuItem())
                    }
                    val menuItem = createMenuItem(option)
                    menuItem.addEventHandler(ActionEvent.ACTION) { runNonRow(option) }
                    contextMenu.items.add(menuItem)
                    added = true
                }
            }
            needSep = needSep || added
        }

    }

    protected fun createMenuItem(option: Option): MenuItem {
        val box = BorderPane()
        val label = Label(option.label)
        label.minWidth = 250.0
        box.center = label
        box.right = Label("  " + option.code)
        val item = CustomMenuItem(box, true)
        return item
    }

    fun runNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
        refresher = Refresher()
        doNonRow(option, prompt = prompt, newTab = newTab)
    }

    fun runNonRow(code: String, prompt: Boolean = false, newTab: Boolean = false): Boolean {
        refresher = Refresher()
        val option = OptionsManager.findNonRowOption(code, tool.optionsName)
        if (option == null || option.isRow) {
            return false
        }
        doNonRow(option, prompt = prompt, newTab = newTab)
        return true
    }

    protected fun doNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
        try {
            val result = option.runNonRow(tool)

            process(result,
                    newTab = newTab || option.newTab,
                    prompt = prompt || option.prompt,
                    refresh = option.refresh)
        } catch(e: Exception) {
            handleException(e)
        }
    }

    protected fun handleException(e: Exception) {
        tool.toolPane?.halfTab?.projectTab?.projectTabs?.projectWindow?.handleException(e)
    }

    protected fun process(
            result: Any?,
            newTab: Boolean,
            prompt: Boolean,
            refresh: Boolean) {

        when (result) {
            is Exec -> {
                processExec(result, refresh)
            }
            is OSCommand -> {
                processExec(Exec(result), refresh)
            }
            is Tool -> {
                processTool(result, newTab, prompt)
            }
            is Task -> {
                processTask(result, prompt, refresh)
            }
            else -> {
                if (refresh) {
                    refresher.add()
                    refresher.onFinished()
                }
            }
        }
    }

    protected fun processTool(returnedTool: Tool, newTab: Boolean, prompt: Boolean) {

        val halfTab = tool.toolPane?.halfTab
        val projectTabs = halfTab?.projectTab?.projectTabs

        // Reuse the current tool where possible, otherwise copy the tool
        val newTool = if (returnedTool !== tool || newTab) returnedTool.copy() else tool
        newTool.resolveParameters(tool.resolver)

        if (newTab) {

            val tab = projectTabs?.addAfter(tool.toolPane!!.halfTab.projectTab, newTool, run = !prompt)
            tab?.isSelected

        } else {
            halfTab?.changeTool(newTool, prompt)
        }

    }

    protected fun processTask(task: Task, prompt: Boolean, refresh: Boolean) {

        task.resolveParameters(tool.resolver)

        if (refresh) {
            refresher.add()
            task.taskRunner.listen {
                refresher.onFinished()
            }
        }

        var checked: Boolean
        try {
            task.check()
            checked = true
        } catch (e: Exception) {
            checked = false
        }

        // Either prompt the Task, or run it straight away
        if (prompt || !checked) {
            TaskPrompter(task).placeOnStage(Stage())
        } else {
            task.taskRunner.run()
        }

    }

    protected fun processExec(exec: Exec, refresh: Boolean) {
        if (refresh) {
            refresher.add()
            exec.listen { refresher.onFinished() }
        }
        exec.start()
    }

    /**
     * This is the "simple" version, but RowOptionsRunner has a more complex version, which handles
     * batches.
     */
    open inner class Refresher {

        open fun add() {}

        open fun onFinished() {
            tool.taskRunner.runIfNotAlready()
        }
    }

}