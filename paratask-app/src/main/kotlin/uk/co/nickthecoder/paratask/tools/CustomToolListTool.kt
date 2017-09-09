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
import uk.co.nickthecoder.paratask.RegisteredTaskFactory
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.RowFilter
import uk.co.nickthecoder.paratask.util.uncamel

class CustomToolListTool : ListTableTool<CustomToolRow>() {

    override val taskD = TaskDescription("customToolList", description = "Create a list of customised tools")

    val toolsP = MultipleParameter("tools") {
        val labelP = StringParameter("label")
        val toolP = TaskParameter("tool", taskFactory = RegisteredTaskFactory())
        val compoundP = CompoundParameter("toolDetails")
        compoundP.addParameters(labelP, toolP)
        compoundP
    }

    override val rowFilter = RowFilter<CustomToolRow>(this, columns, CustomToolRow("", this))

    init {
        taskD.addParameters(toolsP)

        columns.add(Column<CustomToolRow, String>("label") { row -> row.label })
        columns.add(Column<CustomToolRow, ImageView>("icon", label = "") { row -> ImageView(row.tool.icon) })
        columns.add(Column<CustomToolRow, String>("toolName") { row -> row.tool.taskD.name.uncamel() })
        columns.add(Column<CustomToolRow, String>("parameters") { row -> parameters(row.tool) })
    }

    override fun run() {
        list.clear()
        toolsP.value.forEach { compound ->
            val toolP = compound.find("tool") as TaskParameter
            val labelP = compound.find("label") as StringParameter
            if (toolP is ValueParameter<*>) {
                val task = toolP.value
                if (task is Tool) {
                    list.add(CustomToolRow(labelP.value, task))
                }
            }
        }
    }

    fun parameters(tool: Tool): String {
        return tool.valueParameters().map { "${it.name}=${it.value?.toString() ?: it.expression ?: ""}" }.joinToString()
    }
}

data class CustomToolRow(val label: String, val tool: Tool) {}

// Note this tool cannot be run from the command line, because the arguments would be too cumbersome.
// Therefore there is no main function.
