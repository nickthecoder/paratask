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
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.table.WrappedRow

class RowOptionsRunner<in R : Any>(tool: Tool) : OptionsRunner(tool) {

    fun buildContextMenu(contextMenu: ContextMenu, rows: List<R>) {

        val firstRow = if (rows.isEmpty()) null else rows[0]

        contextMenu.items.clear()

        val optionsName = tool.optionsName
        val topLevelOptions = OptionsManager.getTopLevelOptions(optionsName)

        var needSep = false

        topLevelOptions.listFileOptions().filter { it.acceptRow(firstRow) }.forEach { fileOptions ->
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
        val option = OptionsManager.findOptionForRow(".", tool.optionsName, row) ?: return
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
                tool.taskRunner.runIfNotAlready()
            }
        }

        fun complete() {
            batchComplete = true
            if (count == 0) {
                tool.taskRunner.runIfNotAlready()
            }
        }

    }
}