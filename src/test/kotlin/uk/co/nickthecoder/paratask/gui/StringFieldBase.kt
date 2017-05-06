package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.parameter.StringParameter

open class StringFieldBase : MyGuiTest() {

    val task = EmptySimpleTask()

    val required = StringParameter("required")
    val optional = StringParameter("optional", required = false)
    val initialA = StringParameter("initialA")

    override fun getRootNode(): Parent {

        task.taskD.addParameters(required, optional, initialA)
        initialA.stringValue = "A"

        val taskPrompter = TaskPrompter(task)
        return taskPrompter.root
    }
}