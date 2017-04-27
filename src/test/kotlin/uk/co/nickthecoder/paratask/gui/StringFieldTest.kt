package uk.co.nickthecoder.paratask.gui

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.co.nickthecoder.paratask.parameter.StringParameter

class StringFieldTest : StringFieldBase() {

    @Before
    fun waitForScene() {
        waitForScene("Optional")
    }

    @Test
    fun initialValues() {
        assertEquals("", findTextField("optional").text)
        assertEquals("", optional.value(values))

        assertEquals("", findTextField("required").text)
        assertEquals("", required.value(values))

        assertEquals("A", findTextField("initialA").text)
        assertEquals("A", initialA.value(values))
    }

    @Test
    fun emptyOptionalValues() {
        var field = findTextField("optional")
        val paramter = optional
        val error = findError("optional")

        clickAndClear(field).type("x")
        assertEquals("x", paramter.value(values))
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(false, error.isVisible)
    }

    @Test
    fun emptyRequiredValues() {
        val field = findTextField("required")
        val parameter = required
        val error = findError("required")

        clickAndClear(field).type("x")
        assertEquals("x", parameter.value(values))
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(true, error.isVisible)
        assertEquals("Required", error.text)
    }
}