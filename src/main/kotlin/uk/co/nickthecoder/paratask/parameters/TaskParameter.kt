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
import uk.co.nickthecoder.paratask.TaskGroup
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.parameters.fields.TaskField
import uk.co.nickthecoder.paratask.util.escapeNL
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.paratask.util.unescapeNL

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
        required: Boolean = true,
        val programmable: Boolean = true,
        val taskFactory: TaskFactory = RegisteredTaskFactory.instance)

    : AbstractValueParameter<Task?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    /**
     * Format is a set of lines. The first line is the creation string.
     * The subsequent lines are in the form name=value
     * where name is the name of the parameter and value is the string representation of the parameter's value
     * If the parameter is an expression, then then name is prefixed by an additional "="
     * For example :
     *
     * co.uk.nickthecoder.paratask.MyTool
     * param1=1
     * =param2=1+1
     */
    override val converter = object : StringConverter<Task?>() {
        override fun fromString(str: String): Task? {
            val lines = (if (str.endsWith('\n')) str.substring(0, str.length - 1) else str).split('\n')
            val task = taskFactory.creationStringToTask.get(lines[0])
            for (i in 1..lines.size - 2) {
                var line = lines[i]
                var isExpression = false
                if (line.startsWith("=")) {
                    line = line.substring(1)
                    isExpression = true
                }
                val eq = line.indexOf("=")
                if (eq > 0) {
                    val parameterName = line.substring(0, eq)
                    val parameterValue = line.substring(eq + 1)
                    val parameter = task?.taskD?.root?.find(parameterName)
                    if (parameter is ValueParameter<*>) {
                        if (isExpression) {
                            parameter.expression = parameterValue.unescapeNL()
                        } else {
                            parameter.stringValue = parameterValue.unescapeNL()
                        }
                    }
                }
            }
            return task
        }

        override fun toString(task: Task?): String {
            val builder = StringBuilder()
            builder.appendln(task?.creationString() ?: "")
            task?.valueParameters()?.forEach { parameter ->
                val str = parameter.expression ?: parameter.stringValue
                builder.appendln("${parameter.name}=${str.escapeNL()}")
            }
            return builder.toString()
        }
    }

    override fun errorMessage(v: Task?): String? = null

    override fun isStretchy() = false

    override fun createField(): LabelledField = TaskField(this)

    override fun toString(): String = "Task" + super.toString()

    override fun copy() = TaskParameter(name = name, label = label, description = description, value = value, required = required, taskFactory = taskFactory)
}

interface TaskFactory {

    val creationStringToTask: Map<String, Task>

    fun topLevelTasks(): List<Task>

    fun taskGroups(): List<TaskGroup>

}

class RegisteredTaskFactory : TaskFactory {

    override val creationStringToTask = mutableMapOf<String, Task>()

    init {
        TaskRegistry.listGroups().forEach { group ->
            group.listToolsAndTasks().forEach { task ->
                creationStringToTask.put(task.creationString(), task)
            }
        }
    }

    override fun topLevelTasks(): List<Task> {
        val list = mutableListOf<Task>()
        list.addAll(TaskRegistry.topLevel.listTools())
        list.addAll(TaskRegistry.topLevel.listTasks())
        return list
    }

    override fun taskGroups(): List<TaskGroup> {
        return TaskRegistry.listGroups().filter { it != TaskRegistry.topLevel }
    }

    companion object {
        val instance = RegisteredTaskFactory()
    }
}
