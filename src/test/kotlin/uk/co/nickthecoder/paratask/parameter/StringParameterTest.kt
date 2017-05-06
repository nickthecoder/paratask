package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class StringParameterTest : MyTest() {

    val optional = StringParameter("optional", required = false)

    val required = StringParameter("required")

    @Test
    fun setValueOptional() {
        optional.value = "a"
        assertEquals("a", optional.value)

        optional.value = ""
        assertEquals("", optional.value)
        assertNull(optional.errorMessage())

    }

    @Test
    fun setValueRequired() {
        required.value = "a"
        assertEquals("a", required.value)

        required.value = ""
        assertEquals("Required", required.errorMessage())

        required.value = "a"
        assertNull(required.errorMessage())

    }

    @Test
    fun getStringValue() {
        required.value = "a"
        assertEquals("a", required.stringValue)
        optional.value = ""
        assertEquals("", optional.stringValue)
    }
}