package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.Values

open class IntFieldBase : MyGuiTest() {

    val task = EmptySimpleTask()

    val required = IntParameter("required", required = true)
    val optional = IntParameter("optional", required = false)
    val oneToTenRequired = IntParameter("oneToTenRequired", range = 1..10, required = true)
    val oneToTenOptional = IntParameter("oneToTenOptional", range = 1..10, required = false)
    val tenToTenRequired = IntParameter("tenToTenRequired", range = -10..10, required = true)
    val tenToTenOptional = IntParameter("tenToTenOptional", range = -10..10, required = false)
    val initial5 = IntParameter("initial5")

    lateinit var values: Values

    override fun getRootNode(): Parent {

        task.taskD.addParameters(
                required, optional,
                oneToTenRequired, oneToTenOptional,
                tenToTenRequired, tenToTenOptional,
                initial5
        )

        values = task.taskD.createValues()
        initial5.parameterValue(values).value = 5

        val taskPrompter = TaskPrompter(task, values)
        return taskPrompter.root
    }
}