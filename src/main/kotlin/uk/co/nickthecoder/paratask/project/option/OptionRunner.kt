package uk.co.nickthecoder.paratask.project.option

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.WrappedList
import uk.co.nickthecoder.paratask.project.table.WrappedRow
import uk.co.nickthecoder.paratask.project.task.TerminalTool
import uk.co.nickthecoder.paratask.util.Command

class OptionRunner(val tool: Tool) {

    fun runRow(option: Option, wrappedRow: WrappedRow<*>, prompt: Boolean = false, newTab: Boolean = false) {
        val row = wrappedRow.row!!

        doit(option, row, prompt = prompt, newTab = newTab)
    }

    fun runDefault(wrappedRow: WrappedRow<*>, prompt: Boolean = false, newTab: Boolean = false) {
        val row = wrappedRow.row!!
        val option = getOption(".")
        if (option == null) {
            return
        }
        doit(option, row, prompt = prompt, newTab = newTab)
    }

    fun runNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
        doit(option, prompt = prompt, newTab = newTab)
    }


    private fun getOption(code: String): Option? = OptionsManager.findOption(code, tool.optionsName)

    private fun doit(option: Option, prompt: Boolean, newTab: Boolean) {
        val result = option.runNonRow(tool)

        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }

    private fun doit(option: Option, row: Any, prompt: Boolean, newTab: Boolean) {
        val result = option.run(tool, row = row)

        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }

    private fun process(
            result: Any?,
            newTab: Boolean,
            prompt: Boolean,
            refresh: Boolean) {

        when (result) {
            is Command -> {
                val terminal = TerminalTool()
                terminal.changeCommand(result)
                return process(terminal, newTab = newTab, prompt = prompt, refresh = refresh)
            }
            is Tool -> {
                processTool(result, newTab, prompt, refresh)
            }
            is Task -> {
                processTask(result, prompt, refresh)
            }
            is Runnable -> {
                processRunnable(result, refresh)
            }
            else -> {
                // Do nthing
            }
        }
    }

    private fun processTool(returnedTool: Tool, newTab: Boolean, prompt: Boolean, refresh: Boolean) {
        val halfTab = tool.toolPane?.halfTab
        val projectTabs = halfTab?.projectTab?.projectTabs

        // Reuse the current tool where possible, otherwise copy the tool
        var newTool = if (returnedTool !== tool || newTab) returnedTool.copy() else tool

        if (newTab) {

            // TODO Insert after current tab
            projectTabs?.addTool(newTool)

        } else {
            halfTab?.changeTool(newTool)
        }

    }

    private fun processTask(task: Task, prompt: Boolean, refresh: Boolean) {

        // TODO Add a listener is refresh
        //listen(currentTool, task);

        // Either prompt the Task, or run it straight away
        if (prompt) {
            TaskPrompter(task).placeOnStage(Stage())
        } else {

        }
        // TODO Implement running tasks

    }

    private fun processRunnable(runnable: Runnable, refresh: Boolean) {

    }
}