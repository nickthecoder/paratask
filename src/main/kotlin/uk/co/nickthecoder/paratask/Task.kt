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

import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.project.TaskRunner

interface Task {

    val taskRunner: TaskRunner

    val taskD: TaskDescription

    fun check()

    /**
     * Check that the parameters are all valid. This must be called before a task is run.
     * If Task's have their own validation, above that supplied by the Parameters themselves, then override
     * this method, and throw a ParameterException if the Parameter are not acceptable.
     * <p>
     * In most cases, this method does nothing.
     * </p>
     */
    fun customCheck()

    fun run(): Any?

    fun valueParameters(): List<ValueParameter<*>> = taskD.root.valueParameters()

    fun parameters(): List<Parameter> = taskD.root.descendants()

    fun copy(): Task {

        val copy = this::class.java.newInstance()
        copy.taskD.copyValuesFrom(taskD)
        return copy
    }


    fun creationString(): String = this::class.java.name

    companion object {
        fun create(creationString: String): Task {
            return Class.forName(creationString).newInstance() as Task
        }
    }
}
