package uk.co.nickthecoder.paratask.project

interface ParametersPane {

    val tool: uk.co.nickthecoder.paratask.Tool

    val taskForm: uk.co.nickthecoder.paratask.parameters.fields.TaskForm

    fun run(): Boolean

    fun attached(toolPane: ToolPane)

    fun detaching()
}
