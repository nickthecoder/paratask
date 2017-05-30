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

package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.parameters.fields.TaskField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * Allows a Task to be chosen from a list of tasks, and shows a button, which when pressed, allows
 * the tasks' parameters to be filled in by the user.
 * This is used when editing TaskOptions as part of the OptionsTool
 */
class TaskParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Task? = null,
        required: Boolean = true)

    : AbstractValueParameter<Task?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    val creationStringToTask = mutableMapOf<String, Task>()

    init {
        TaskRegistry.listGroups().forEach { group ->
            group.listToolsAndTasks().forEach { task ->
                creationStringToTask.put(task.creationString(), task)
            }
        }
    }

    override val converter = object : StringConverter<Task?>() {
        override fun fromString(str: String): Task? {
            return creationStringToTask.get(str)
        }

        override fun toString(task: Task?): String {

            return task?.creationString() ?: ""
        }
    }

    override fun errorMessage(v: Task?): String? = null

    override fun isStretchy() = false

    override fun createField(): LabelledField = TaskField(this)

    override fun toString(): String = "Int" + super.toString()

    override fun copy() = TaskParameter(name = name, label = label, description = description, value = value, required = required)
}

