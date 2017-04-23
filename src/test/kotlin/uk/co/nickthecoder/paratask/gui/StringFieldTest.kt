package uk.co.nickthecoder.paratask.gui

import org.junit.Test
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class StringFieldTest : StringFieldBase() {

    @Before
    fun waitForScene() {
        waitForScene("optional")
    }

    @Test
    fun initialValues() {
        assertEquals("", findTextField("optional").text)
        assertEquals("", taskD.optional.value)

        assertEquals("", findTextField("required").text)
        assertEquals("", taskD.required.value)

        assertEquals("A", findTextField("initialA").text)
        assertEquals("A", taskD.initialA.value)
    }

    @Test
    fun emptyOptionalValues() {
        val field = findTextField("optional")
        val parameter = taskD.optional
        val error = findError("optional")

        clickAndClear(field).type("x")
        assertEquals("x", parameter.value)
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(false, error.isVisible)
    }

    @Test
    fun emptyRequiredValues() {
        val field = findTextField("required")
        val parameter = taskD.required
        val error = findError("required")

        clickAndClear(field).type("x")
        assertEquals("x", parameter.value)
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(true, error.isVisible)
        assertEquals("Required", error.text)
    }
}