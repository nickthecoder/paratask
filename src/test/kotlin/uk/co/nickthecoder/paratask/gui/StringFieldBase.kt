package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values

open class StringFieldBase : MyGuiTest() {

    val task = EmptySimpleTask()

    val required = StringParameter("required")
    val optional = StringParameter("optional", required = false)
    val initialA = StringParameter("initialA")

    lateinit var values : Values

    override fun getRootNode(): Parent {

        println("Creating root node")

        task.taskD.addParameters(required, optional, initialA)
        values = task.taskD.createValues()
        values.get("initialA")!!.stringValue = "A"

        val taskPrompter = TaskPrompter(task, values)
        return taskPrompter.root
    }
}