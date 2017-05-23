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

class TaskGroup(val label: String) {

    private val tasks = mutableListOf<Task>()

    private val tools = mutableListOf<Tool>()

    fun listTasks() = tasks.map { it.copy() }

    fun listTools() = tools.map { it.copy() }

    fun listToolsAndTasks() = listTools() + listTasks()

    fun addTasks(vararg tasks: Task) {
        this.tasks.addAll(tasks)
    }

    fun addTools(vararg tools: Tool) {
        this.tools.addAll(tools)
    }
}
