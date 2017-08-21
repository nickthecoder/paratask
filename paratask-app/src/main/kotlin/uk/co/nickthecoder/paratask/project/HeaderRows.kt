package uk.co.nickthecoder.paratask.project

import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.focusNext

class HeaderRows(val tool: Tool, val rows: List<HeaderRow>) : VBox() {

    init {
        styleClass.add("header")

        rows.forEach() {
            children.add(it)
        }

        val lastRowIndex = rows.size - 1
        if (lastRowIndex >= 0) {
            val lastRow = rows[lastRowIndex]
            lastRow.addRunButton(tool)
        }
    }

    fun focus() {
        rows.firstOrNull()?.focusNext()
    }
}
