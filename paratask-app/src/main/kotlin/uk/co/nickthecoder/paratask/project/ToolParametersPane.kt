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

interface ToolParametersPane : ParametersPane {
    val tool: Tool
}

class ToolParametersPane_Impl(override val tool: Tool)

    : ParametersPane_Impl(tool), ToolParametersPane {

    override fun run(): Boolean {

        task.resolveParameters(toolPane.halfTab.projectTab.projectTabs.projectWindow.project.resolver)

        if (taskForm.check()) {

            toolPane.halfTab.pushHistory(tool)
            tool.taskRunner.run()
            return true
        }
        return false
    }

}
