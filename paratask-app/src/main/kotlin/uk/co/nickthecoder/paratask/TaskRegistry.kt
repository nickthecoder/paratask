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

import uk.co.nickthecoder.paratask.parameters.GroupParameter

object TaskRegistry {

    private val taskGroups = mutableListOf<TaskAndToolGroup>()

    private val registered = mutableSetOf<Registers>()

    /**
     * Maps creation strings to creation strings. The key is the alias, and the value is the actual creation string.
     * This allows tools to be renamed without breaking existing project files.
     * Used by Project.load
     */
    private val toolAliases = mutableMapOf<String, String>()

    val home = TaskAndToolGroup("Home")

    val topLevel = TaskAndToolGroup("Top Level")

    val misc = TaskAndToolGroup("Miscellaneous")

    val projectData = GroupParameter("projectData")

    init {
        register(ParaTaskCore.instance)

        addGroup(home)
        addGroup(topLevel)
        addGroup(misc)
    }

    fun listGroups(): List<TaskAndToolGroup> = taskGroups

    fun addGroup(group: TaskAndToolGroup) {
        taskGroups.add(group)
    }

    fun register(item: Registers) {
        if (!registered.contains(item)) {
            registered.add(item)
            item.register()
        }
    }

    fun aliasTool(tool: Tool, alias: String) {
        toolAliases.put(alias, tool.creationString())
    }

    fun createTool(creationString: String): Tool {
        val cs = toolAliases.get(creationString) ?: creationString
        return Tool.create(cs)
    }
}
