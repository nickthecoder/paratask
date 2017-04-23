package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Spinner
import javafx.scene.input.KeyCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.loadui.testfx.GuiTest
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.parameter.IntParameter

open class IntFieldBase : MyGuiTest() {

    lateinit var taskPrompter: TaskPrompter

    lateinit var taskD: ExampleTaskD

    override fun getRootNode(): Parent {
        taskD = ExampleTaskD()
        taskPrompter = TaskPrompter(EmptySimpleTask(taskD))

        return taskPrompter.root
    }

    class ExampleTaskD : TaskDescription() {

        val required = IntParameter("required", required = true)
        val optional = IntParameter("optional", required = false)
        val oneToTenRequired = IntParameter("oneToTenRequired", range = 1..10, required = true)
        val oneToTenOptional = IntParameter("oneToTenOptional", range = 1..10, required = false)
        val tenToTenRequired = IntParameter("tenToTenRequired", range = -10..10, required = true)
        val tenToTenOptional = IntParameter("tenToTenOptional", range = -10..10, required = false)
        val initial5 = IntParameter("initial5", value = 5)

        init {
            addParameters(
                    required, optional,
                    oneToTenRequired, oneToTenOptional,
                    tenToTenRequired, tenToTenOptional,
                    initial5
            )
        }
    }
}