package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Spinner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.loadui.testfx.GuiTest
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.IntParameter

class IntControlTest : GuiTest() {

    lateinit var taskPrompter: TaskPrompter

    lateinit var task: Task

    override fun getRootNode(): Parent {
        task = ExampleTask()
        taskPrompter = TaskPrompter(task)

        return taskPrompter.root
    }

    @Test
    fun findFields() {
        val optionalField = find<Field>(".field-optional")
        assertTrue(optionalField is Field)
    }

    fun findControl(parameterName: String): Node {
        return find<Node>(".control", find(".field-${parameterName}"))
    }

    fun findSpinner(parameterName: String): Spinner<*> {
        return findControl(parameterName) as Spinner<*>
    }

    @Test
    fun isSpinner() {
        val firstControl = find<Node>(".control")
        assertTrue(firstControl is Spinner<*>)

        assertTrue(findControl("optional") is Spinner<*>)
        assertTrue(findControl("required") is Spinner<*>)
    }

    @Test
    fun initialValues() {
        val firstControl = find<Node>(".control")
        assertTrue(firstControl is Spinner<*>)

        assertNull(findSpinner("optional").value)
        assertEquals(0, findSpinner("required").value)
        assertNull(findSpinner("oneToTenOptional").value)
        assertEquals(1, findSpinner("oneToTenRequired").value)
        assertNull(findSpinner("tenToTenOptional").value)
        assertEquals(0, findSpinner("tenToTenRequired").value)
    }

    class ExampleTask : Task() {

        val required = IntParameter("required", required = true)
        val optional = IntParameter("optional", required = false)
        val oneToTenRequired = IntParameter("oneToTenRequired", range = 1..10, required = true)
        val oneToTenOptional = IntParameter("oneToTenOptional", range = 1..10, required = false)
        val tenToTenRequired = IntParameter("tenToTenRequired", range = -10..10, required = true)
        val tenToTenOptional = IntParameter("tenToTenOptional", range = -10..10, required = false)

        init {
            addParameters(
                    required, optional,
                    oneToTenRequired, oneToTenOptional,
                    tenToTenRequired, tenToTenOptional
            )
        }

        override fun body() {}
    }
}