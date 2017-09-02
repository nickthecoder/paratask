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

    /**
     * Maps creation strings to creation strings. The key is the alias, and the value is the actual creation string.
     * This allows tools to be renamed without breaking existing project files.
     * Used by Project.load
     */
    private val taskAliases = mutableMapOf<String, String>()

    val home = TaskGroup("Home")

    val topLevel = TaskGroup("Top Level")

    val misc = TaskGroup("Miscellaneous")

    init {
        register(ParaTaskCore.instance)

        addGroup(home)
        addGroup(topLevel)
        addGroup(misc)
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

    fun aliasTool(tool: Tool, alias: String) {
        taskAliases.put(alias, tool.creationString())
    }

    fun createTool(creationString: String): Tool {
        val cs = taskAliases.get(creationString) ?: creationString
        return Tool.create(cs)
    }

    fun createTask(creationString: String): Task {
        val cs = taskAliases.get(creationString) ?: creationString
        return Task.create(cs)
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
