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
import uk.co.nickthecoder.paratask.gui.MyTabPane

interface ToolPane {

    var tool: Tool

    val tabPane: MyTabPane<MinorTab>

    val halfTab: HalfTab

    val parametersPane: ParametersPane

    val parametersTab: ToolPane_Impl.ParametersTab

    fun resultsTool(): Tool

    fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>)

    fun addResults(results: Results): ResultsTab

    fun addResults(results: Results, index: Int): ResultsTab

    fun attached(halfTab: HalfTab)

    fun detaching()

    fun selected()

    fun isAttached(): Boolean

    fun nextTab()

    fun prevTab()

    fun selectTab(index: Int)

    fun focusHeader()

    fun focusResults()

    /**
     * Prevents the focus changing the next time the tool is run.
     */
    var skipFocus: Boolean

    fun currentResults(): Results?
}
