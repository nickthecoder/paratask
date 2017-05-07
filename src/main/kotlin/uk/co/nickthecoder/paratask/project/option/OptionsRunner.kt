package uk.co.nickthecoder.paratask.project.option

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.project.TaskListener
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.WrappedRow
import uk.co.nickthecoder.paratask.project.task.TerminalTool
import uk.co.nickthecoder.paratask.util.Command

class OptionsRunner<R : Any>(val tool: Tool) {


    fun runDefault( row: R, prompt: Boolean = false, newTab: Boolean = false) {
        val option = OptionsManager.findOption(".", tool.optionsName)

        if (option == null) {
            return
        }
        runRow(option, row, prompt = prompt, newTab = newTab)
    }

    fun runRow(option: Option, row: R, prompt: Boolean = false, newTab: Boolean = false) {
        val result = option.run(tool, row = row)

        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }

    fun runMultiple(option: Option, rows: List<R>, newTab: Boolean, prompt: Boolean) {
        val result = option.runMultiple(tool, rows )
        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }

    fun runNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
        val result = option.runNonRow(tool)

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
                processTool(result, newTab, prompt)
            }
            is Task -> {
                processTask(result, prompt, refresh)
            }
            else -> {
                // Do nthing
            }
        }
    }

    private fun processTool(returnedTool: Tool, newTab: Boolean, prompt: Boolean) {
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
        if (prompt) {
            // TODO Show the parameters pane
        }
    }

    private fun processTask(task: Task, prompt: Boolean, refresh: Boolean) {

        // Either prompt the Task, or run it straight away
        if (prompt) {
            TaskPrompter(task).placeOnStage(Stage())
        } else {
            val taskRunner = ThreadedTaskRunner(task)
            if (refresh) {
                taskRunner.listeners.add(object : TaskListener {
                    override fun ended() {
                        tool.taskRunner.run()
                    }
                })
            }
            taskRunner.run()
        }
        // TODO Implement running tasks

    }

}