package uk.co.nickthecoder.paratask.project.option

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.project.TaskListener
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

open class OptionsRunner(val tool: Tool) {

    fun runNonRow(code: String, prompt: Boolean = false, newTab: Boolean = false): Boolean {
        val option = OptionsManager.findOption(code, tool.optionsName)
        if (option == null || option.isRow) {
            return false
        }
        runNonRow(option, prompt = prompt, newTab = newTab)
        return true
    }

    fun runNonRow(option: Option, prompt: Boolean = false, newTab: Boolean = false) {
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
            is String -> {
                println(result)
            }
            else -> {
                // Do nthing
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
            projectTabs?.addTool(newTool)

        } else {
            halfTab?.changeTool(newTool)
        }
        if (prompt) {
            // TODO Show the parameters pane
        }
    }

    protected fun processExec(exec: Exec, refresh: Boolean) {
        if (refresh) {
            exec.listen { tool.taskRunner.run() }
        }
        exec.start()
    }

    protected fun processTask(task: Task, prompt: Boolean, refresh: Boolean) {

        if (refresh) {
            task.taskRunner.listen {
                tool.taskRunner.run()
            }
        }

        // Either prompt the Task, or run it straight away
        if (prompt) {
            TaskPrompter(task).placeOnStage(Stage())
        } else {
            task.taskRunner.run()
        }

    }

}