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

import uk.co.nickthecoder.paratask.TaskDescription

class RootParameter(val taskD: TaskDescription, description: String)

    : SimpleGroupParameter("root", description = description) {

    override fun findTaskD(): TaskDescription = taskD

    override fun findRoot(): RootParameter? = this

    fun valueParameters(): List<ValueParameter<*>> {
        val result = mutableListOf<ValueParameter<*>>()

        fun addAll(group: GroupParameter) {
            group.children.forEach { child ->
                if (child is ValueParameter<*>) {
                    result.add(child)
                }
                if (child is GroupParameter) {
                    addAll(child)
                }
            }
        }

        addAll(this)
        return result
    }

    override fun copy() :RootParameter {
        val result =RootParameter(taskD = taskD, description = description)
        copyChildren(result)
        return result
    }
}
