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
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.RegisteredTaskFactory
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolBarTool
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.AbstractTaskButton
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.util.uncamel

class CustomTaskListTool : ListTableTool<CustomTaskListTool.Item>(), ToolBarTool {

    override val taskD = TaskDescription("customToolList", description = "Create a list of customised tools")

    override var toolBarConnector: ToolBarToolConnector? = null

    val toolBarSideP = ChoiceParameter<Side?>("toolbar", value = null, required = false)
            .nullableEnumChoices("None")
    override var toolBarSide by toolBarSideP

    val toolsP = MultipleParameter("tools") {
        Item()
    }.asListDetail(labelFactory = { rowParameters ->
        val label = rowParameters.labelP.value
        if (label.isBlank()) {
            (rowParameters.find("tool") as TaskParameter).value?.taskD?.label ?: "<new item>"
        } else {
            label
        }
    })

    private val exampleRow = Item()
    override val rowFilter = RowFilter(this, columns, exampleRow)

    init {
        taskD.addParameters(toolBarSideP, toolsP)

        columns.add(Column<Item, ImageView>("icon", label = "", getter = { row -> ImageView(row.taskP.value!!.icon) }))
        columns.add(Column<Item, String>("label", getter = { row -> row.labelP.value }))
        columns.add(Column<Item, String>("toolName", getter = { row -> row.taskP.value!!.taskD.name.uncamel() }))
        columns.add(Column<Item, String>("parameters", getter = { row -> parameters(row.taskP.value!!) }))

        toolBarSideP.listen {
            toolsP.value.forEach { compound ->
                val newTabP = compound.find("newTab")
                newTabP?.hidden = toolBarSideP.value == null
            }
        }
    }

    override fun run() {
        list.clear()
        toolsP.innerParameters.forEach { item ->
            val task = item.taskP.value
            if (task != null) {
                list.add(item)
            }
        }

        if (showingToolbar()) {
            Platform.runLater {
                updateToolbar(list.map { row ->
                    AbstractTaskButton.createToolOrTaskButton(
                            toolBarConnector!!.projectWindow, row.taskP.value!!, row.labelP.value, newTab = row.newTabP.value!!)
                })
            }
        }
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        if (toolBarConnector == null) {
            toolBarConnector = ToolBarToolConnector(toolPane.halfTab.projectTab.projectTabs.projectWindow, this, false)
        }
    }


    fun parameters(task: Task): String {
        return task.valueParameters().map {
            // Regex trancates any parameter value with one or more new line characters.
            "${it.name}=${it.value?.toString()?.replace(Regex("(?s)\n.*\\z"), " …") ?: it.expression ?: ""}"
        }.joinToString()
    }


    /**
     * The inner parameter within the MultipleParameter 'toolsP'
     */
    class Item : MultipleGroupParameter("toolDetails", label = "") {

        val labelP = StringParameter("label", required = false)
        val descriptionP = StringParameter("description", required = false)
        val taskP = TaskParameter("task", taskFactory = RegisteredTaskFactory()).addAliases("tool")
        val newTabP = BooleanParameter("newTab", value = true)

        init {
            addParameters(labelP, descriptionP, taskP, newTabP)
        }
    }
}


// Note this tool cannot be run from the command line, because the arguments would be too cumbersome.
// Therefore there is no main function.
