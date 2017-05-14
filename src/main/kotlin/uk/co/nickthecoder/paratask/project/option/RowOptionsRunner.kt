package uk.co.nickthecoder.paratask.project.option

import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import uk.co.nickthecoder.paratask.project.Tool

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
                    menuItem.addEventHandler(ActionEvent.ACTION) {
                        runRows(option, rows)
                    }
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

    fun runDefault(row: R, prompt: Boolean = false, newTab: Boolean = false) {
        val option = OptionsManager.findOption(".", tool.optionsName)
        if (option == null) {
            return
        }
        runRow(option, row, prompt = prompt, newTab = newTab)
    }

    fun runRows(option: Option, rows: List<R>, prompt: Boolean = false, newTab: Boolean = false) {
        if (option.isMultiple) {
            runMultiple(option, rows, prompt = prompt, newTab = newTab)
        } else {
            for (row in rows) {
                runRow(option, row, prompt = prompt, newTab = newTab)
            }
        }
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