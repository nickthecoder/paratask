package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.input.KeyCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

open class IntFieldTest : IntFieldBase() {

    @Before
    fun waitForScene() {
        waitForScene("Optional")
    }

    @Test
    fun findFields() {
        val optionalField = find<LabelledField>(".field-optional")
        assertTrue(optionalField is LabelledField)
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
        assertNull(optional.parameterValue(values).value)

        assertEquals(0, findSpinner("required").value)
        assertEquals(0, required.parameterValue(values).value)

        assertNull(findSpinner("oneToTenOptional").value)
        assertEquals(1, findSpinner("oneToTenRequired").value)

        assertNull(findSpinner("tenToTenOptional").value)
        assertEquals(0, findSpinner("tenToTenRequired").value)

        assertEquals(5, initial5.parameterValue(values).value)
        assertEquals(5, findSpinner("initial5").value)
    }

    @Test
    fun upFromInitial() {
        click(findSpinner("optional")).type(KeyCode.UP)
        assertEquals(0, findSpinner("optional").value)
        //assertEquals(0, optional.value)

        click(findSpinner("required")).type(KeyCode.UP)
        assertEquals(1, findSpinner("required").value)
        //assertEquals(1, required.value)

        click(findSpinner("oneToTenOptional")).type(KeyCode.UP)
        assertEquals(1, findSpinner("oneToTenOptional").value)
        //assertEquals(1, oneToTenOptional.value)

        click(findSpinner("oneToTenRequired")).type(KeyCode.UP)
        assertEquals(2, findSpinner("oneToTenRequired").value)
        //assertEquals(2, oneToTenRequired.value)
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
        assertEquals(1, oneToTenOptional.value)
    }

    @Test
    fun downFromMin2() {

        val tenToTenRequired = findSpinner("tenToTenRequired")
        assertEquals(0, tenToTenRequired.value)

        click(tenToTenRequired).type(KeyCode.DELETE)
        click(tenToTenRequired).type(KeyCode.BACK_SPACE)
        type("-9")
        assertEquals(-9, tenToTenRequired.value)
        assertEquals(-9, tenToTenRequired.value)

        typeSequential(KeyCode.DOWN)

        assertEquals(-10, tenToTenRequired.value)
        assertEquals(-10, tenToTenRequired.value)

        typeSequential(KeyCode.DOWN)

        assertEquals(-10, tenToTenRequired.value)
        assertEquals(-10, tenToTenRequired.value)
    }

    @Test
    fun upToMax() {

        val tenToTenRequired = findSpinner("tenToTenRequired")
        assertEquals(0, tenToTenRequired.value)

        click(tenToTenRequired).type(KeyCode.DELETE)
        click(tenToTenRequired).type(KeyCode.BACK_SPACE)
        type("9")
        assertEquals(9, tenToTenRequired.value)
        assertEquals(9, tenToTenRequired.value)

        typeSequential(KeyCode.UP)

        assertEquals(10, tenToTenRequired.value)
        assertEquals(10, tenToTenRequired.value)

        typeSequential(KeyCode.UP)

        assertEquals(10, tenToTenRequired.value)
        assertEquals(10, tenToTenRequired.value)
    }


    @Test
    fun invalidValues() {

        val tenToTenRequired = findSpinner("tenToTenRequired")
        val error = findError("tenToTenRequired")

        clickAndClear(tenToTenRequired).type("9")
        assertEquals(9, tenToTenRequired.value)
        assertEquals(false, error.isVisible)

        clickAndClear(tenToTenRequired).type("a")
        assertEquals(true, error.isVisible)
        assertEquals("Not an integer", error.text)

        clickAndClear(tenToTenRequired).type("1.2")
        assertEquals(true, error.isVisible)
        assertEquals("Not an integer", error.text)

        typeSequential(KeyCode.BACK_SPACE, KeyCode.BACK_SPACE) // Text is now "1"
        //sleep(1000)
        assertEquals(false, error.isVisible)

        typeSequential(KeyCode.BACK_SPACE) // Text is now blank
        assertEquals(true, error.isVisible)
        assertEquals("Required", error.text)

        clickAndClear(tenToTenRequired).type("-11")
        assertEquals(true, error.isVisible)
        assertEquals("Must be in the range -10..10", error.text)

        clickAndClear(tenToTenRequired).type("11")
        assertEquals(true, error.isVisible)
        assertEquals("Must be in the range -10..10", error.text)
    }


    @Test
    fun invalidValues2() {

        val oneToTenOptional = findSpinner("oneToTenOptional")
        val error = findError("oneToTenOptional")

        clickAndClear(oneToTenOptional).type("9")
        assertEquals(9, oneToTenOptional.value)
        assertEquals(false, error.isVisible)

        clickAndClear(oneToTenOptional).type("a")
        assertEquals(true, error.isVisible)
        assertEquals("Not an integer", error.text)

        clickAndClear(oneToTenOptional).type("1.2")
        assertEquals(true, error.isVisible)
        assertEquals("Not an integer", error.text)

        typeSequential(KeyCode.BACK_SPACE, KeyCode.BACK_SPACE) // Text is now "1"
        assertEquals(false, error.isVisible)

        typeSequential(KeyCode.BACK_SPACE) // Text is now blank
        assertEquals(false, error.isVisible)

        clickAndClear(oneToTenOptional).type("0")
        assertEquals(true, error.isVisible)
        assertEquals("Must be in the range 1..10", error.text)

        clickAndClear(oneToTenOptional).type("11")
        assertEquals(true, error.isVisible)
        assertEquals("Must be in the range 1..10", error.text)
    }

    /**
     * Using Up or down keys does not change the spinners text field when it contains invalid text
     */
    @Test
    fun spinInvalid() {

        val oneToTenOptional = findSpinner("oneToTenOptional")
        val error = findError("oneToTenOptional")

        clickAndClear(oneToTenOptional).type("a")

        type(KeyCode.UP)
        type(KeyCode.DOWN)

        assertEquals("a", oneToTenOptional.editor.text)
        assertEquals(true, error.isVisible)
        assertEquals("Not an integer", error.text)
    }

}