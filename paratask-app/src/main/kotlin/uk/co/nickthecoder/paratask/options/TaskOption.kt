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

package uk.co.nickthecoder.paratask.options

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.tools.NullTask
import java.io.Serializable

class TaskOption(var task: Task)
    : AbstractOption(), Serializable {

    constructor(creationString: String) : this(TaskRegistry.createTask(creationString))

    /**
     * For serialization
     */
    constructor() : this(NullTask())

    override fun run(tool: Tool, row: Any) = tool.evaluateTask(task, row, null)

    override fun runMultiple(tool: Tool, rows: List<Any>) = tool.evaluateTask(task, null, rows)

    override fun runNonRow(tool: Tool) = tool.evaluateTask(task, null, null)

    override fun copy(): TaskOption {
        val result = TaskOption(task.copy())
        this.copyTo(result)
        return result
    }
}
