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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskGroup
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.gui.ButtonGroup
import uk.co.nickthecoder.paratask.parameters.TaskParameter
import uk.co.nickthecoder.paratask.project.Action
import uk.co.nickthecoder.paratask.project.ProgrammingModeTaskPrompter

class TaskField(override val parameter: TaskParameter) : LabelledField(parameter) {

    private val taskButton = Button("...")

    private val contextMenu = ContextMenu()

    val taskLabel = Label("")

    val button = Button("Parameters")

    init {

        val box = HBox()
        box.styleClass.add("task-field")
        box.children.addAll(taskLabel, taskButton, button)
        this.control = box

        taskButton.addEventFilter(ActionEvent.ACTION) { onChooseTask() }

        button.addEventHandler(ActionEvent.ACTION) { onEditParameters() }
        buildContextMenu()
        taskButton.contextMenu = contextMenu
        parameter.value?.let { taskLabel.text = it.taskD.label }
    }

    private fun onEditParameters() {
        val task = parameter.value
        if (task != null) {
            task.taskD.programmingMode = true
            val taskPrompter = ProgrammingModeTaskPrompter(task)
            taskPrompter.placeOnStage(Stage())
        }
    }

    private fun buildContextMenu() {
        TaskRegistry.topLevel.listTools().forEach { addTask(it) }
        TaskRegistry.topLevel.listTasks().forEach { addTask(it) }

        contextMenu.items.add(SeparatorMenuItem())
        TaskRegistry.listGroups().forEach { addGroup(it) }
    }

    private fun addGroup(taskGroup: TaskGroup) {
        // Skip the top-level tools as these are added as top-leve items
        if (taskGroup === TaskRegistry.topLevel) return

        val menu = Menu(taskGroup.label)
        taskGroup.listTools().forEach { addTask(it, menu) }
        taskGroup.listTasks().forEach { addTask(it, menu) }
        contextMenu.items.add(menu)
    }

    private fun addTask(task: Task, parent: Any = contextMenu) {
        val menuItem = MenuItem(task.taskD.label)
        menuItem.addEventHandler(ActionEvent.ACTION) {
            parameter.value = task
            taskLabel.text = task.taskD.label
        }

        if (parent is ContextMenu) {
            parent.items.add(menuItem)
        } else if (parent is Menu) {
            parent.items.add(menuItem)
        }
    }

    fun onChooseTask() {
        contextMenu.show(taskButton, Side.BOTTOM, 0.0, 0.0)
    }
}
