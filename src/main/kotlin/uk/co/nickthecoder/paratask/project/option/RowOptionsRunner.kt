package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.project.Tool

class RowOptionsRunner<R : Any>(tool: Tool) : OptionsRunner(tool) {

    fun runDefault(row: R, prompt: Boolean = false, newTab: Boolean = false) {
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
        val result = option.runMultiple(tool, rows)
        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }

}