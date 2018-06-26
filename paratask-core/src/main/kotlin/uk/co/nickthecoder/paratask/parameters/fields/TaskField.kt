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
import javafx.scene.layout.HBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskGroup
import uk.co.nickthecoder.paratask.gui.EditTaskPrompter
import uk.co.nickthecoder.paratask.parameters.TaskParameter

class TaskField(val taskParameter: TaskParameter)
    : ParameterField(taskParameter) {

    private val taskButton = Button("...")

    private val contextMenu = ContextMenu()

    val taskLabel = Label("")

    val button = Button("Parameters")

    override fun createControl(): HBox {

        val box = HBox()
        box.styleClass.add("task-field")
        box.children.addAll(taskLabel, taskButton, button)
        this.control = box

        taskButton.addEventFilter(ActionEvent.ACTION) { onChooseTask() }

        button.addEventHandler(ActionEvent.ACTION) { onEditParameters() }
        buildContextMenu()
        taskButton.contextMenu = contextMenu
        taskParameter.value?.let { taskLabel.text = it.taskD.label }

        return box
    }

    private fun onEditParameters() {
        val task = taskParameter.value
        if (task != null) {
            if (taskParameter.programmable) {
                task.taskD.programmingMode = true
            }
            val taskPrompter = EditTaskPrompter(task, taskParameter.scriptVariables) {
                // TODO Why are we firing a value changed? Hmm.
                taskParameter.parameterListeners.fireValueChanged(taskParameter, null)
            }
            taskPrompter.placeOnStage(Stage())
        }
    }

    private fun buildContextMenu() {
        taskParameter.taskFactory.topLevelTasks.forEach { addTask(it) }
        contextMenu.items.add(SeparatorMenuItem())
        taskParameter.taskFactory.taskGroups.forEach { addGroup(it) }
    }

    private fun addGroup(taskGroup: TaskGroup) {
        val menu = Menu(taskGroup.label)
        taskGroup.listTasks().forEach { addTask(it, menu) }
        contextMenu.items.add(menu)
    }

    private fun addTask(task: Task, parent: Any = contextMenu) {
        val menuItem = MenuItem(task.taskD.label)
        menuItem.addEventHandler(ActionEvent.ACTION) {
            taskParameter.value = task
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
