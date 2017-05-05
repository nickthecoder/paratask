package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.WrappedList
import uk.co.nickthecoder.paratask.project.table.WrappedRow
import uk.co.nickthecoder.paratask.project.task.TerminalTool
import uk.co.nickthecoder.paratask.util.Command

class OptionRunner(val tool: Tool) {

    fun rowOptions(list: WrappedList<*>, prompt: Boolean = false, newTab: Boolean = false) {
        for (wrappedRow in list) {
            val code = wrappedRow.code
            if (code != "") {
                val row = wrappedRow.row
                println("Running code '${code}' on row '${row}'")
            }
        }
        //
    }

    fun runDefault(wrappedRow: WrappedRow<*>, prompt: Boolean = false, newTab: Boolean = false) {
        val row = wrappedRow.row!!

        println("Running default option on row '${row}'")
        doit(".", row, prompt = prompt, newTab = newTab)
    }

    fun nonRowOption(code: String, prompt: Boolean = false, newTab: Boolean = false) {
        println("Running non-row code '${code}'")
    }


    private fun getOption(code: String): Option? = OptionsManager.findOption(code, tool.optionsName)

    private fun doit(code: String, row: Any, prompt: Boolean, newTab: Boolean) {
        val option = getOption(code)
        if (option == null) {
            println("Option '${code}' not found")
            return
        }

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

        if (result is Command) {
            val terminal = TerminalTool()
            terminal.changeCommand(result)
            return process(terminal, newTab = newTab, prompt = prompt, refresh = refresh)
        }

        val halfTab = tool.toolPane?.halfTab
        val projectTabs = halfTab?.projectTab?.projectTabs

        if (result is Tool) {
            // Reuse the current tool where possible, otherwise copy the tool
            var newTool = if (result !== tool || newTab) result.copy() else tool

            if (newTab) {

                // TODO Insert after current tab
                projectTabs?.addTool(newTool)

            } else {
                halfTab?.changeTool(newTool)
            }

        } else if (result is Task) {

            val task = result

            // TODO Add a listener is refresh
            //listen(currentTool, task);

            // Either prompt the Task, or run it straight away
            // TODO Implement running tasks

        } else if (result is Runnable) {

            // TODO Implement running arbitrary Runnables
            // TODO If refresh, refresh the list when it finishes
        }

    }
}