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

    // NOTE, We need to use "by lazy", because without it, we risk trying to access the TaskRegistry while it
    // is still being constructed, leading to infinite recursion.

    override val creationStringToTask by lazy {
        creationStringMapping()
    }

    override val topLevelTasks by lazy {
        TaskRegistry.topLevel.listTasks()
    }

    override val taskGroups by lazy { TaskRegistry.listGroups().filter { it != TaskRegistry.topLevel } }


    private fun creationStringMapping(): Map<String, Task> {
        val map = mutableMapOf<String, Task>()
        TaskRegistry.listGroups().forEach { group ->
            group.listTasks().forEach { task ->
                map.put(task.creationString(), task)
            }
        }
        return map
    }

    companion object {
        val instance = RegisteredTaskFactory()
    }
}
