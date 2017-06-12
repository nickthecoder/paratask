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

package uk.co.nickthecoder.paratask.tools

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.TaskParameter
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.uncamel

class ListTool : AbstractTableTool<Tool>() {

    override val taskD = TaskDescription("listTool", description = "Pre-Build Tools")

    val toolsP = MultipleParameter("tools") { TaskParameter("tool") }

    init {
        taskD.addParameters(toolsP)
    }

    override fun createColumns() {
        columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
        columns.add(Column<Tool, String>("name") { tool -> tool.taskD.name.uncamel() })
        columns.add(Column<Tool, String>("description") { tool -> tool.taskD.description })
    }

    override fun run() {
        list.clear()
        toolsP.value.forEach { task ->
            if (task is Tool) {
                list.add(task)
            }
        }
    }

}

// Note this tool cannot be run from the command line, because the arugments would be too cumbersome.
// Therefore there is no main function.
