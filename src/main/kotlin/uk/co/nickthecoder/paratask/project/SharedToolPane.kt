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

import uk.co.nickthecoder.paratask.Tool

class SharedToolPane(override var tool: Tool) : ToolPane {

    val shared: ToolPane = tool.toolPane!!

    override val halfTab: HalfTab
        get() = shared.halfTab

    override val parametersPane: ParametersPane
        get() = shared.parametersPane

    override fun resultsTool(): Tool = shared.resultsTool()

    override fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>) {
        shared.replaceResults(resultsList, oldResultsList)
    }

    override fun attached(halfTab: HalfTab) {}

    override fun detaching() {}

    override fun selected() {}

    override fun toggleParameters() {
        shared.toggleParameters()
    }

    override fun nextTab() {
        shared.nextTab()
    }

    override fun prevTab() {
        shared.prevTab()
    }

    override fun selectTab(index: Int) {
        shared.selectTab(index)
    }

    override fun focusHeader() {
        shared.focusHeader()
    }

    override fun focusResults() {
        shared.focusResults()
    }
}