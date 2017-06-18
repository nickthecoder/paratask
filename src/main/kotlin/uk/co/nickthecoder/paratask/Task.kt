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

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.options.GroovyScript
import uk.co.nickthecoder.paratask.options.Helper
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.project.TaskRunner

interface Task {

    val taskRunner: TaskRunner

    val taskD: TaskDescription

    /**
     * This is used to resolve OTHER tasks created by this task.
     * For example, if this task/tool has a directory, the child tasks can resolve files based on that directory.
     */
    var resolver: ParameterResolver

    /**
     * Resolves this task's parameters based on the context of whoever created this task.
     * Tasks may optionally extend this method, performing their own resolution (maybe based on their own resolver),
     * but this would be unusual.
     */
    fun resolveParameters(resolver: ParameterResolver) {
        valueParameters().forEach {
            resolveParameter(resolver, it)
        }
    }

    fun resolveParameter(resolver: ParameterResolver, parameter: ValueParameter<*>) {
        if (parameter is ValueParameter<*>) {
            resolver.resolve(parameter)
            if (parameter is MultipleParameter<*>) {
                parameter.innerParameters.forEach { resolveParameter(resolver, it) }
            }
        }
    }

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

    /**
     * Copy this task, replacing any expressions with their evaluated values
     */
    fun evaluate(tool: Tool, row: Any?, rows: List<Any>?): Task {
        val copy = this.copy()

        for (parameter in copy.valueParameters()) {
            evaluateParameter(parameter, tool, row = row, rows = rows)
        }
        return copy
    }

    private fun evaluateParameter(parameter: ValueParameter<*>, tool: Tool, row: Any?, rows: List<Any>?) {

        if (parameter is MultipleParameter<*> && parameter.expression == null) {
            parameter.innerParameters.filter { it.expression != null }.forEach { innerParameter ->
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
                parameter.expression = null
            }
        }
    }

    fun creationString(): String = this::class.java.name

    companion object {
        fun create(creationString: String): Task {
            return Class.forName(creationString).newInstance() as Task
        }
    }
}
