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

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.util.uncamel

class CustomToolListTool : ListTableTool<CustomToolRow>(), ToolBarTool {

    override val taskD = TaskDescription("customToolList", description = "Create a list of customised tools")

    override var toolBarConnector: ToolBarToolConnector? = null

    val toolBarSideP = ChoiceParameter<Side?>("toolbar", value = Side.TOP, required = false)
            .nullableEnumChoices("None")
    override var toolBarSide by toolBarSideP

    val toolsP = MultipleParameter("tools") {

        val labelP = StringParameter("label", required = false)
        val toolP = TaskParameter("tool", taskFactory = RegisteredTaskFactory())
        val compoundP = CompoundParameter("toolDetails")
        val newTabP = BooleanParameter("newTab", value = true)

        compoundP.addParameters(labelP, toolP, newTabP)
        compoundP
    }

    private val exampleRow = CustomToolRow("", this, true)
    override val rowFilter = RowFilter<CustomToolRow>(this, columns, exampleRow)

    init {
        taskD.addParameters(toolBarSideP, toolsP)

        columns.add(Column<CustomToolRow, ImageView>("icon", label = "", getter = { row -> ImageView(row.task.icon) }))
        columns.add(Column<CustomToolRow, String>("label", getter = { row -> row.label }))
        columns.add(Column<CustomToolRow, String>("toolName", getter = { row -> row.task.taskD.name.uncamel() }))
        columns.add(Column<CustomToolRow, String>("parameters", getter = { row -> parameters(row.task) }))

        toolBarSideP.listen {
            toolsP.value.forEach { compound ->
                val newTabP = compound.find("newTab")
                newTabP?.hidden = toolBarSideP.value == null
            }
        }
    }

    override fun run() {
        list.clear()
        toolsP.value.forEach { compound ->
            val toolP = compound.find("tool") as TaskParameter
            val labelP = compound.find("label") as StringParameter
            val newTabP = compound.find("newTab") as BooleanParameter
            if (toolP is ValueParameter<*>) {
                val task = toolP.value
                if (task != null) {
                    list.add(CustomToolRow(labelP.value, task, newTabP.value == true))
                }
            }
        }
        updateToolbar()
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        if (toolBarConnector == null) {
            toolBarConnector = ToolBarToolConnector(toolPane.halfTab.projectTab.projectTabs.projectWindow, this)
        }
    }

    override fun toolBarButtons(projectWindow: ProjectWindow): List<Button> {
        return list.map { row ->
            AbstractTaskButton.createToolOrTaskButton(projectWindow, row.task, row.label, newTab = row.newTab)
        }
    }

    fun parameters(task: Task): String {
        return task.valueParameters().map { "${it.name}=${it.value?.toString() ?: it.expression ?: ""}" }.joinToString()
    }
}

data class CustomToolRow(val label: String, val task: Task, val newTab: Boolean) {}

// Note this tool cannot be run from the command line, because the arguments would be too cumbersome.
// Therefore there is no main function.
