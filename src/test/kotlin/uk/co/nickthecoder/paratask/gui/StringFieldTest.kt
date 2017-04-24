package uk.co.nickthecoder.paratask.gui

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.co.nickthecoder.paratask.parameter.StringParameter

class StringFieldTest : StringFieldBase() {

    @Before
    fun waitForScene() {
        waitForScene("optional")
    }

    @Test
    fun initialValues() {
        assertEquals("", findTextField("optional").text)
        assertEquals("", optional.valueFrom(values).value)

        assertEquals("", findTextField("required").text)
        assertEquals("", required.valueFrom(values).value)

        assertEquals("A", findTextField("initialA").text)
        assertEquals("A", initialA.valueFrom(values).value)
    }

    @Test
    fun emptyOptionalValues() {
        var field = findTextField("optional")
        val paramter = optional
        val value = paramter.valueFrom(values)
        val error = findError("optional")

        clickAndClear(field).type("x")
        assertEquals("x", value.value)
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
        assertEquals("x", parameter.valueFrom(values).value)
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(true, error.isVisible)
        assertEquals("Required", error.text)
    }
}