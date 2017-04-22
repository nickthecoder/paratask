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
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.IntParameter

open class IntControlTest : IntControlBase() {

    @Test
    fun findFields() {
        val optionalField = find<Field>(".field-optional")
        assertTrue(optionalField is Field)
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
        assertNull(findSpinner("optional").value)
        assertNull(task.optional.value)

        assertEquals(0, findSpinner("required").value)
        assertEquals(0, task.required.value)

        assertNull(findSpinner("oneToTenOptional").value)
        assertEquals(1, findSpinner("oneToTenRequired").value)

        assertNull(findSpinner("tenToTenOptional").value)
        assertEquals(0, findSpinner("tenToTenRequired").value)
    }

    @Test
    fun upFromInitial() {
        click(findSpinner("optional")).type(KeyCode.UP)
        assertEquals(0, findSpinner("optional").value)
        assertEquals(0, task.optional.value)

        click(findSpinner("required")).type(KeyCode.UP)
        assertEquals(1, findSpinner("required").value)
        assertEquals(1, task.required.value)

        click(findSpinner("oneToTenOptional")).type(KeyCode.UP)
        assertEquals(1, findSpinner("oneToTenOptional").value)
        assertEquals(1, task.oneToTenOptional.value)

        click(findSpinner("oneToTenRequired")).type(KeyCode.UP)
        assertEquals(2, findSpinner("oneToTenRequired").value)
        assertEquals(2, task.oneToTenRequired.value)
    }

    @Test
    fun downFromInitial() {
        click(findSpinner("optional")).type(KeyCode.DOWN)
        assertEquals(0, findSpinner("optional").value)

        click(findSpinner("required")).type(KeyCode.DOWN)
        assertEquals(-1, findSpinner("required").value)

        click(findSpinner("oneToTenOptional")).type(KeyCode.DOWN)
        assertEquals(1, findSpinner("oneToTenOptional").value)

        click(findSpinner("oneToTenRequired")).type(KeyCode.DOWN)
        assertEquals(1, findSpinner("oneToTenRequired").value)
    }

    @Test
    fun downFromMin1() {

        val oneToTenOptional = findSpinner("oneToTenOptional")
        click(oneToTenOptional).type(KeyCode.DELETE, KeyCode.BACK_SPACE)
        type("2")

        typeSequential(KeyCode.DOWN, KeyCode.DOWN)

        assertEquals(1, oneToTenOptional.value)
        assertEquals(1, task.oneToTenOptional.value)
    }

    @Test
    fun downFromMin2() {

        val tenToTenRequired = findSpinner("tenToTenRequired")
        assertEquals(0, task.tenToTenRequired.value)

        click(tenToTenRequired).type(KeyCode.DELETE)
        click(tenToTenRequired).type(KeyCode.BACK_SPACE)
        type("-9")
        assertEquals(-9, tenToTenRequired.value)
        assertEquals(-9, task.tenToTenRequired.value)

        typeSequential(KeyCode.DOWN)

        assertEquals(-10, tenToTenRequired.value)
        assertEquals(-10, task.tenToTenRequired.value)

        typeSequential(KeyCode.DOWN)

        assertEquals(-10, tenToTenRequired.value)
        assertEquals(-10, task.tenToTenRequired.value)
    }

}