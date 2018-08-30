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

object TaskFactory {

    /**
     * Maps creation strings to creation strings. The key is the alias, and the value is the actual creation string.
     * This allows tools to be renamed without breaking existing project files.
     * Used by Project.load
     */
    private val taskAliases = mutableMapOf<String, String>()

    fun addAlias(alias: String, correctName: String) {
        taskAliases[alias] = correctName
    }

    fun addAlias(alias: String, task: Task) {
        addAlias(alias, task.creationString())
    }

    fun createTask(creationString: String): Task {
        val cs = taskAliases[creationString] ?: creationString
        return Class.forName(cs).newInstance() as Task
    }

}
