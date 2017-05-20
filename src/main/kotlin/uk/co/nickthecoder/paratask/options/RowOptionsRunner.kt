package uk.co.nickthecoder.paratask.options

import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.table.WrappedRow

class RowOptionsRunner<in R : Any>(tool: Tool) : OptionsRunner(tool) {

    fun buildContextMenu(contextMenu: ContextMenu, rows: List<R>) {

        contextMenu.items.clear()

        val optionsName = tool.optionsName
        val topLevelOptions = OptionsManager.getTopLevelOptions(optionsName)

        var needSep = false

        for (fileOptions in topLevelOptions.listFileOptions()) {
            var added = false
            for (option in fileOptions.listOptions()) {
                if (option.isRow) {
                    if (needSep) {
                        needSep = false
                        contextMenu.items.add(SeparatorMenuItem())
                    }
                    val menuItem = createMenuItem(option)
                    menuItem.addEventHandler(ActionEvent.ACTION) { runRows(option, rows) }
                    contextMenu.items.add(menuItem)
                    added = true
                }
            }
            needSep = needSep || added
        }

        if (contextMenu.items.count() > 0) {
            val menu = Menu("Non-Row Options")
            val temp = ContextMenu()
            createNonRowOptionsMenu(temp)
            menu.items.addAll(temp.items)
            contextMenu.items.add(0, menu)
        } else {
            createNonRowOptionsMenu(contextMenu)
        }
    }

    fun runBatch(batch: Map<Option, List<WrappedRow<R>>>, newTab: Boolean, prompt: Boolean) {
        val batchRefresher = BatchRefresher()
        refresher = batchRefresher

        for ((option, list) in batch) {
            if (option.isMultiple) {
                doMultiple(option, list.map { it.row }, newTab = newTab, prompt = prompt)
            } else {
                list.map { it.row }.forEach {
                    if (option.isRow) {
                        doRow(option, it, newTab = newTab, prompt = prompt)
                    } else {
                        doNonRow(option, newTab = newTab, prompt = prompt)
                    }
                }
            }
        }
        batchRefresher.complete()
    }

    fun runDefault(row: R, prompt: Boolean = false, newTab: Boolean = false) {
        refresher = Refresher()
        val option = OptionsManager.findOption(".", tool.optionsName) ?: return
        doRow(option, row, prompt = prompt, newTab = newTab)
    }

    private fun runRows(option: Option, rows: List<R>, prompt: Boolean = false, newTab: Boolean = false) {
        val batchRefresher = BatchRefresher()
        refresher = batchRefresher

        doRows(option, rows, prompt = prompt, newTab = newTab)

        batchRefresher.complete()
    }

    private fun doRows(option: Option, rows: List<R>, prompt: Boolean = false, newTab: Boolean = false) {
        try {
            if (option.isMultiple) {
                doMultiple(option, rows, prompt = prompt, newTab = newTab)
            } else {
                for (row in rows) {
                    doRow(option, row, prompt = prompt, newTab = newTab)
                }
            }
        } catch(e: Exception) {
            handleException(e)
        }

    }

    private fun doRow(option: Option, row: R, prompt: Boolean = false, newTab: Boolean = false) {
        try {
            val result = option.run(tool, row = row)

            process(result,
                    newTab = newTab || option.newTab,
                    prompt = prompt || option.prompt,
                    refresh = option.refresh)
        } catch(e: Exception) {
            handleException(e)
        }
    }


    private fun doMultiple(option: Option, rows: List<R>, newTab: Boolean, prompt: Boolean) {
        try {
            val result = option.runMultiple(tool, rows)
            process(result,
                    newTab = newTab || option.newTab,
                    prompt = prompt || option.prompt,
                    refresh = option.refresh)
        } catch(e: Exception) {
            handleException(e)
        }
    }


    inner class BatchRefresher : Refresher() {

        var count = 0

        var batchComplete = false

        override fun add() {
            count++
        }

        override fun onFinished() {
            count--
            if (count == 0 && batchComplete) {
                tool.taskRunner.run()
            }
        }

        fun complete() {
            batchComplete = true
            if (count == 0) {
                tool.taskRunner.run()
            }
        }

    }
}
