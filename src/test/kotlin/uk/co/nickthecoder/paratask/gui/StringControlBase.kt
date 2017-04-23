package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.StringParameter

open class StringControlBase : MyGuiTest() {

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

        init {
            addParameters(required, optional)
        }

        override fun body() {}
    }
}