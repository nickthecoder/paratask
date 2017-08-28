/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.TaskFactory

class RegisteredTaskFactory : TaskFactory {

    override val creationStringToTask = mutableMapOf<String, Task>()

    override val topLevelTasks: List<Task>

    override val taskGroups: List<TaskAndToolGroup> = TaskRegistry.listGroups().filter { it != TaskRegistry.topLevel }

    init {
        TaskRegistry.listGroups().forEach { group ->
            group.listTasks().forEach { task ->
                creationStringToTask.put(task.creationString(), task)
            }
        }

        val list = mutableListOf<Task>()
        list.addAll(TaskRegistry.topLevel.listTools())
        list.addAll(TaskRegistry.topLevel.listTasks())
        topLevelTasks = list
    }

    companion object {
        val instance = RegisteredTaskFactory()
    }
}
