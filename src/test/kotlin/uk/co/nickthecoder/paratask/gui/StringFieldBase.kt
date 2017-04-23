package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter

open class StringFieldBase : MyGuiTest() {

    lateinit var taskPrompter: TaskPrompter

    lateinit var taskD: ExampleTaskD

    override fun getRootNode(): Parent {
        taskD = ExampleTaskD()
        taskPrompter = TaskPrompter(EmptySimpleTask(taskD))

        return taskPrompter.root
    }

    class ExampleTaskD : TaskDescription() {

        val required = StringParameter("required")
        val optional = StringParameter("optional", required = false)
        val initialA = StringParameter("initialA", value = "A")

        init {
            addParameters(required, optional, initialA)
        }
    }
}