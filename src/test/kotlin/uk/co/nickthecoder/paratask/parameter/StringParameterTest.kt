package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class StringParameterTest {

    val optional = StringParameter("optional", required = false)
    val optionalValue = optional.createValue()

    val required = StringParameter("required")
    val requiredValue = required.createValue()

    fun expectParameterException(body: () -> Unit) {
        try {
            body()
            assertNull("Expected a ParameterException to be thrown")
        } catch (e: ParameterException) {
        }
    }

    @Test
    fun setValueOptional() {
        optionalValue.value = "a"
        assertEquals("a", optionalValue.value)

        optionalValue.value = ""
        assertEquals("", optionalValue.value)
        assertNull(optionalValue.errorMessage())

    }

    @Test
    fun setValueRequired() {
        requiredValue.value = "a"
        assertEquals("a", requiredValue.value)

        requiredValue.value = ""
        assertEquals("Required", requiredValue.errorMessage())

        requiredValue.value = "a"
        assertNull(requiredValue.errorMessage())

    }

    @Test
    fun getStringValue() {
        requiredValue.value = "a"
        assertEquals("a", requiredValue.stringValue)
        optionalValue.value = ""
        assertEquals("", optionalValue.stringValue)
    }
}