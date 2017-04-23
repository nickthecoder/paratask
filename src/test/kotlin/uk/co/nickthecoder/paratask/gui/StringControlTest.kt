package uk.co.nickthecoder.paratask.gui

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class StringControlTest : StringControlBase() {

    @Test
    fun initialValues() {
        assertEquals("", findTextField("optional").text)
        assertEquals("", task.optional.value)

        assertEquals("", findTextField("required").text)
        assertEquals("", task.required.value)
    }

    // TODO Put back @Test
    fun emptyOptionalValues() {
        val field = findTextField("optional")
        val parameter = task.optional
        val error = findError("optional")

        clickAndClear(field).type("x")
        assertEquals("x", parameter.value)
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(false, error.isVisible)
    }

    // TODO Put back @Test
    fun emptyRequiredValues() {
        val field = findTextField("required")
        val parameter = task.required
        val error = findError("required")

        clickAndClear(field).type("x")
        assertEquals("x", parameter.value)
        assertEquals(false, error.isVisible)

        clickAndClear(field)
        assertEquals(true, error.isVisible)
        assertEquals("Required", error.text)
    }
}