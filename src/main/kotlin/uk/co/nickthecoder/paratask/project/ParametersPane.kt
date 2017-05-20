package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

interface ParametersPane {

    val tool: Tool

    val taskForm: TaskForm

    fun run(): Boolean

    fun attached(toolPane: ToolPane)

    fun detaching()
}
