package uk.co.nickthecoder.paratask.project.option

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.CopyableTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.project.Tool

class TaskOption(val task: CopyableTask)
    : AbstractOption() {

    constructor(creationString: String) : this(CopyableTask.create(creationString))

    override fun run(tool: Tool, row: Any): Task {
        return createResult(tool, row = row, rows = null)
    }

    override fun runMultiple(tool: Tool, rows: List<Any>): Task {
        return createResult(tool, null, rows)
    }

    override fun runNonRow(tool: Tool): Task {
        return createResult(tool, null, null)
    }

    protected fun createResult(tool: Tool, row: Any?, rows: List<Any>?): CopyableTask {

        val copiedTask = task.copy()
        for (parameter in copiedTask.valueParameters()) {
            evaluateParameter(parameter, tool, row = row, rows = rows)
        }
        return copiedTask
    }

    protected fun evaluateParameter(parameter: ValueParameter<*>, tool: Tool, row: Any?, rows: List<Any>?) {

        if (parameter is MultipleParameter<*> && parameter.expression == null) {
            for (innerParameter in parameter.innerParameters) {
                evaluateParameter(innerParameter, tool, row = row, rows = rows)
            }
        } else {
            parameter.expression?.let { expression ->
                val gscript = GroovyScript(expression)
                val bindings = Binding()
                bindings.setProperty("tool", tool)
                bindings.setProperty("row", row)
                bindings.setProperty("rows", rows)

                parameter.evaluated(gscript.run(bindings))
            }
        }
    }

    override fun copy(): TaskOption {
        val result = TaskOption(task.copy())
        this.copyTo(result)
        return result
    }
}