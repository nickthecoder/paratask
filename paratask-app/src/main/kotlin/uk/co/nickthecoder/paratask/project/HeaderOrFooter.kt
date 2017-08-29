package uk.co.nickthecoder.paratask.project

import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.util.focusNext

open class HeaderOrFooter(vararg rows: HeaderRow) : VBox() {

    constructor(vararg parameters: Parameter) : this(HeaderRow(*parameters))

    val rows: List<HeaderRow> = rows.toList()

    init {
        styleClass.add("header")

        rows.forEach {
            children.add(it)
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
