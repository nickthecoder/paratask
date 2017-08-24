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
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

interface ParametersPane {

    val tool: Tool

    val taskForm: TaskForm

    /**
     * This is how all tools should be run, as it checks the parameters, and displays them if there is a problem.
     * Do NOT call Tool's run method directly, or TaskRunner's run methods either.
     **/
    fun run(): Boolean

    fun runIfNotAlreadyRunning(): Boolean

    fun attached(toolPane: ToolPane)

    fun detaching()

    fun focus()
}
