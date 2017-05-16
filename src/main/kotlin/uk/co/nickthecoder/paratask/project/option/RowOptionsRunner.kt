package uk.co.nickthecoder.paratask.project.option

import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.WrappedRow

class RowOptionsRunner<R : Any>(tool: Tool) : OptionsRunner(tool) {

    fun buildContextMenu(contextMenu: ContextMenu, rows: List<R>) {

        contextMenu.getItems().clear()

        val optionsName = tool.optionsName
        val topLevelOptions = OptionsManager.getTopLevelOptions(optionsName)

        var needSep = false

        for (fileOptions in topLevelOptions.listFileOptions()) {
            var added = false
            for (option in fileOptions.listOptions()) {
                if (option.isRow) {
                    if (needSep) {
                        needSep = false
                        contextMenu.getItems().add(SeparatorMenuItem())
                    }
                    val menuItem = createMenuItem(option)
                    menuItem.addEventHandler(ActionEvent.ACTION) { runRows(option, rows) }
                    contextMenu.getItems().add(menuItem)
                    added = true
                }
            }
            needSep = needSep || added
        }

        if (contextMenu.getItems().count() > 0) {
            val menu = Menu("Non-Row Options")
            val temp = ContextMenu()
            createNonRowOptionsMenu(temp)
            menu.getItems().addAll(temp.getItems())
            contextMenu.getItems().add(0, menu)
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
                for (wrappedRow in list) {
                    val row = wrappedRow.row
                    if (option.isRow) {
                        doRow(option, row, newTab = newTab, prompt = prompt)
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
        val option = OptionsManager.findOption(".", tool.optionsName)
        if (option == null) {
            return
        }
        doRow(option, row, prompt = prompt, newTab = newTab)
    }

    protected fun runRows(option: Option, rows: List<R>, prompt: Boolean = false, newTab: Boolean = false) {
        val batchRefresher = BatchRefresher()
        refresher = batchRefresher

        doRows(option, rows, prompt = prompt, newTab = newTab)

        batchRefresher.complete()
    }

    protected fun doRows(option: Option, rows: List<R>, prompt: Boolean = false, newTab: Boolean = false) {
        if (option.isMultiple) {
            doMultiple(option, rows, prompt = prompt, newTab = newTab)
        } else {
            for (row in rows) {
                doRow(option, row, prompt = prompt, newTab = newTab)
            }
        }
    }

    protected fun doRow(option: Option, row: R, prompt: Boolean = false, newTab: Boolean = false) {
        val result = option.run(tool, row = row)

        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }


    protected fun doMultiple(option: Option, rows: List<R>, newTab: Boolean, prompt: Boolean) {
        val result = option.runMultiple(tool, rows)
        process(result,
                newTab = newTab || option.newTab,
                prompt = prompt || option.prompt,
                refresh = option.refresh)
    }


    inner class BatchRefresher() : Refresher() {

        var count = 0

        var batchComplete = false

        override fun add() {
            count++
        }

        override fun onFinished() {
            count--
            println("Dec ${count} ${batchComplete}")
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