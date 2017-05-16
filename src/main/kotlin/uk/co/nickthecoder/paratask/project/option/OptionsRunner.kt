package uk.co.nickthecoder.paratask.project.option

import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

open class OptionsRunner(val tool: Tool) {

    protected var refresher: Refresher = Refresher()

    fun createNonRowOptionsMenu(contextMenu: ContextMenu) {

        contextMenu.getItems().clear()

        val optionsName = tool.optionsName
        val topLevelOptions = OptionsManager.getTopLevelOptions(optionsName)

        var needSep = false

        for (fileOptions in topLevelOptions.listFileOptions()) {
            var added = false
            for (option in fileOptions.listOptions()) {
                if (!option.isRow) {
                    if (needSep) {
                        needSep = false
                        contextMenu.getItems().add(SeparatorMenuItem())
                    }
                    val menuItem = createMenuItem(option)
                    menuItem.addEventHandler(ActionEvent.ACTION) { runNonRow(option) }
                    contextMenu.getItems().add(menuItem)
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
        val option = OptionsManager.findOption(code, tool.optionsName)
        if (option == null || option.isRow) {
            return false
        }
        doNonRow(option, prompt = prompt, newTab = newTab)
        return true
    }

    protected fun doNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
        val result = option.runNonRow(tool)

        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
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
            is Command -> {
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
                    tool.taskRunner.run()
                }
            }
        }
    }

    protected fun processTool(returnedTool: Tool, newTab: Boolean, prompt: Boolean) {
        val halfTab = tool.toolPane?.halfTab
        val projectTabs = halfTab?.projectTab?.projectTabs

        // Reuse the current tool where possible, otherwise copy the tool
        var newTool = if (returnedTool !== tool || newTab) returnedTool.copy() else tool

        if (newTab) {

            // TODO Insert after current tab
            projectTabs?.addAfter(tool.toolPane!!.halfTab.projectTab, newTool)

        } else {
            halfTab?.changeTool(newTool)
        }
        if (prompt) {
            // TODO Show the parameters pane
        }
    }

    protected fun processExec(exec: Exec, refresh: Boolean) {
        if (refresh) {
            refresher.add()
            exec.listen { refresher.onFinished() }
        }
        exec.start()
    }

    protected fun processTask(task: Task, prompt: Boolean, refresh: Boolean) {

        if (refresh) {
            refresher.add()
            task.taskRunner.listen {
                refresher.onFinished()
            }
        }

        // Either prompt the Task, or run it straight away
        if (prompt) {
            TaskPrompter(task).placeOnStage(Stage())
        } else {
            task.taskRunner.run()
        }

    }


    /**
     * This is the "simple" version, but RowOptionsRunner has a more complex version, which handles
     * batches.
     */
    open inner class Refresher {

        open fun add() {}

        open fun onFinished() {
            tool.taskRunner.run()
        }
    }

}