package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.project.TaskPrompter
import uk.co.nickthecoder.paratask.tools.NullTask

open class IntFieldBase : MyGuiTest() {

    val task = NullTask()

    val required = IntParameter("required", required = true)
    val optional = IntParameter("optional", required = false)
    val oneToTenRequired = IntParameter("oneToTenRequired", range = 1..10, required = true)
    val oneToTenOptional = IntParameter("oneToTenOptional", range = 1..10, required = false)
    val tenToTenRequired = IntParameter("tenToTenRequired", range = -10..10, required = true)
    val tenToTenOptional = IntParameter("tenToTenOptional", range = -10..10, required = false)
    val initial5 = IntParameter("initial5")

    override fun getRootNode(): Parent {

        task.taskD.addParameters(
                required, optional,
                oneToTenRequired, oneToTenOptional,
                tenToTenRequired, tenToTenOptional,
                initial5
        )

        initial5.value = 5

        val taskPrompter = TaskPrompter(task)
        return taskPrompter.root
    }
}