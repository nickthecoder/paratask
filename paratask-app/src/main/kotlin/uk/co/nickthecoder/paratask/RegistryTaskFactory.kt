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

import uk.co.nickthecoder.paratask.parameters.TaskParameterFactory

class RegisteredTaskFactory : TaskParameterFactory {

    private var cachedMapping: Map<String, Task>? = null

    override val creationStringToTask: Map<String, Task>
        get () {
            if (cachedMapping == null) {
                val newMap = mutableMapOf<String, Task>()
                cachedMapping = newMap
                TaskRegistry.listGroups().forEach { group ->
                    group.listTasks().forEach { task ->
                        newMap[task.creationString()] = task
                    }
                }
            }
            return cachedMapping!!
        }

    override val topLevelTasks by lazy {
        TaskRegistry.topLevel.listTasks()
    }

    override val taskGroups by lazy { TaskRegistry.listGroups().filter { it != TaskRegistry.topLevel } }

    companion object {
        val instance = RegisteredTaskFactory()
    }
}
