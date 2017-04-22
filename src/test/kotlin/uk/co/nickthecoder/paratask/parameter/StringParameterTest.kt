package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class StringParameterTest {

    val optional = StringParameter("optional", required = false)

    val required = StringParameter("required")

    fun expectParameterException(body: () -> Unit) {
        try {
            body()
            assertNull("Expected a ParameterException to be thrown")
        } catch (e: ParameterException) {
        }
    }

    @Test
    fun setValueOptional() {
        optional.value = "a"
        assertEquals("a", optional.value)

        optional.value = ""
        assertEquals("", optional.value)
        assertNull(optional.errorMesssage())

        optional.value = null
        assertEquals("Null values not allowed", optional.errorMesssage())

    }

    @Test
    fun setValueRequired() {
        required.value = "a"
        assertEquals("a", required.value)

        required.value = ""
        assertEquals("Required", required.errorMesssage())

        required.value = "a"
        assertNull(required.errorMesssage())

        required.value = null
        assertEquals("Null values not allowed", required.errorMesssage())
    }

    @Test
    fun getStringValue() {
        required.value = "a"
        assertEquals("a", required.getStringValue())
        optional.value = ""
        assertEquals("", optional.getStringValue())
    }
}