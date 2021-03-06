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

package uk.co.nickthecoder.paratask

object TaskRegistry {

    private val taskGroups = mutableListOf<TaskGroup>()

    private val registered = mutableSetOf<Registers>()

    val home = TaskGroup("Home")

    val topLevel = TaskGroup("Top Level")

    init {
        register(ParaTaskCore.instance)

        addGroup(home)
        addGroup(topLevel)
    }

    fun listGroups(): List<TaskGroup> = taskGroups

    fun addGroup(group: TaskGroup) {
        taskGroups.add(group)
    }

    fun register(item: Registers) {
        if (!registered.contains(item)) {
            registered.add(item)
            item.register()
        }
    }

    fun allTasks(): Collection<Task> {
        val tasks = hashMapOf<String, Task>()

        listGroups().forEach {
            tasks.putAll(it.listTasks().map { Pair(it.taskD.name, it) })
        }
        return tasks.values
    }

    /**
     * Finds a registered task by its name.
     * First, the name is used to look for a registered task with the same creation string.
     * If none were found, then the name is compared to the registered tasks' TaskDescription's name.
     */
    fun findTask(name: String): Task? {
        return taskGroups.map { taskGroup ->
            taskGroup.listTasks().firstOrNull { it.creationString() == name } ?:
                    taskGroup.listTasks().firstOrNull { it.taskD.name == name }
        }.firstOrNull { it != null }
    }

}
