package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.StringParameter

open class StringFieldBase : MyGuiTest() {

    lateinit var taskPrompter: TaskPrompter

    lateinit var task: ExampleTask

    override fun getRootNode(): Parent {
        task = ExampleTask()
        taskPrompter = TaskPrompter(task)

        return taskPrompter.root
    }

    class ExampleTask : Task() {

        val required = StringParameter("required")
        val optional = StringParameter("optional", required = false)
        val initialA = StringParameter("initialA", value="A")

        init {
            addParameters(required, optional, initialA)
        }

        override fun body() {}
    }
}