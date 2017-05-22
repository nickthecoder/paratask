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

package uk.co.nickthecoder.paratask.options

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.Tool

class TaskOption(var task: Task)
    : AbstractOption() {

    constructor(creationString: String) : this(Task.create(creationString))

    override fun run(tool: Tool, row: Any): Task {
        return createResult(tool, row = row, rows = null)
    }

    override fun runMultiple(tool: Tool, rows: List<Any>): Task {
        return createResult(tool, null, rows)
    }

    override fun runNonRow(tool: Tool): Task {
        return createResult(tool, null, null)
    }

    private fun createResult(tool: Tool, row: Any?, rows: List<Any>?): Task {
        val copiedTask = task.copy()

        for (parameter in copiedTask.valueParameters()) {
            evaluateParameter(parameter, tool, row = row, rows = rows)
        }
        return copiedTask
    }

    private fun evaluateParameter(parameter: ValueParameter<*>, tool: Tool, row: Any?, rows: List<Any>?) {

        // println("Evaluating parameter ${parameter.name} expression=${parameter.expression} value=${parameter.value}")
        if (parameter is MultipleParameter<*> && parameter.expression == null) {
            val innerExpressionsParameters = parameter.innerParameters.filter { it.expression != null }
            for (innerParameter in innerExpressionsParameters) {
                evaluateParameter(innerParameter, tool, row = row, rows = rows)
            }
        } else {
            parameter.expression?.let { expression ->
                val gscript = GroovyScript(expression)
                val bindings = Binding()
                bindings.setProperty("tool", tool)
                bindings.setProperty("row", row)
                bindings.setProperty("rows", rows)
                bindings.setProperty("helper", Helper.instance)

                parameter.evaluated(gscript.run(bindings))
            }
        }

        //println("Evaluated to ${parameter.value}")
    }

    override fun copy(): TaskOption {
        val result = TaskOption(task.copy())
        this.copyTo(result)
        return result
    }
}
