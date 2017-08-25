package uk.co.nickthecoder.paratask.project

import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.util.focusNext

class Header(val tool: Tool, vararg rows: HeaderRow) : VBox() {

    constructor(tool: Tool, vararg parameters: Parameter) : this(tool, HeaderRow(*parameters))

    val rows: List<HeaderRow> = rows.toList()

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
        ParaTaskApp.logFocus("Header focus. rows.firstOrNull().focusNext()")
        rows.firstOrNull()?.focusNext()
    }

    fun detatching() {
        rows.forEach { it.detaching() }
    }
}
