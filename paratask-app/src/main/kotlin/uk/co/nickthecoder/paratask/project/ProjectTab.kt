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

import javafx.scene.input.KeyCodeCombination
import uk.co.nickthecoder.paratask.Tool

interface ProjectTab {

    val tabs: ProjectTabs

    val projectTabs: ProjectTabs

    val left: HalfTab

    val right: HalfTab?

    var tabTemplate: String

    var tabShortcut: KeyCodeCombination?

    fun attached(projectTabs: ProjectTabs)

    fun detaching()

    var isSelected: Boolean

    fun close()

    fun add(tool: Tool)

    fun remove(toolPane: ToolPane)

    fun split(tool: Tool, run: Boolean = true)

    fun split()

    fun splitToggle()


    fun mergeToggle()

    fun duplicateTab()

    fun changed()
}
