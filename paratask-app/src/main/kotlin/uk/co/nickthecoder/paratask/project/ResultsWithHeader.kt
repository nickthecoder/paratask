/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp

class ResultsWithHeader(val results: Results, val headerRows: Header) : Results {

    override val tool
        get() = results.tool

    override val label
        get() = results.label

    override val node = BorderPane()

    override val labelProperty
        get() = results.labelProperty

    init {
        node.center = results.node
        node.top = headerRows
    }

    override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
        results.attached(resultsTab, toolPane)
    }

    override fun detaching() {
        results.detaching()
    }

    override fun selected() {
        results.selected()
    }

    override fun deselected() {
        results.deselected()
    }

    override fun focus() {
        ParaTaskApp.logFocus("ResultsWithHeader focus. results.focus()")
        results.focus()
    }

    override val canClose: Boolean
        get() = results.canClose

    override fun closed() {
        super.closed()
        results.closed()
    }

}
