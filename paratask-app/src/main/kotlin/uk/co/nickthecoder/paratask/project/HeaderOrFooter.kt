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
