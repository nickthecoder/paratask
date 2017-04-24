package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class IntParameterTest {

    val optional = IntParameter("optional", range = 1..10, required = false)
    val optionalValue = optional.createValue()

    val oneToTen = IntParameter("oneToTen", range = 1..10)
    val oneToTenValue = oneToTen.createValue()

    val uptoTen = IntParameter("oneToTen", range = Int.MIN_VALUE..10)
    val uptoTenValue = uptoTen.createValue()

    val fromOne = IntParameter("oneToTen", range = 1..Int.MAX_VALUE)
    val fromOneValue = fromOne.createValue()

    @Test
    fun setBlankStringValue() {
        optionalValue.value = 1
        assertEquals(1, optionalValue.value)

        optionalValue.stringValue = ""
        assertNull(optionalValue.value)

        optionalValue.value = 2
        assertEquals(2, optionalValue.value)
    }

    @Test
    fun setStringValue() {
        oneToTenValue.value = 1
        assertEquals(1, oneToTenValue.value)
        oneToTenValue.stringValue = "4"
        assertEquals(4, oneToTenValue.value)
        oneToTenValue.stringValue = ""
        assertNull(oneToTenValue.value)
    }

    @Test
    fun getStringValue() {
        oneToTenValue.value = 1
        assertEquals("1", oneToTenValue.stringValue)
        oneToTenValue.stringValue = "4"
        assertEquals("4", oneToTenValue.stringValue)
        optionalValue.value = null
        assertEquals("", optionalValue.stringValue)
    }

    @Test
    fun check_1To10() {
        oneToTenValue.value = 1
        assertNull(oneToTenValue.errorMessage())
        oneToTenValue.value = 10
        assertNull(oneToTenValue.errorMessage())
        oneToTenValue.value = 11
        assertEquals("Must be in the range 1..10", oneToTenValue.errorMessage())
        oneToTenValue.value = 0
        assertEquals("Must be in the range 1..10", oneToTenValue.errorMessage())
    }

    @Test
    fun check_upto10() {
        uptoTenValue.value = -1
        assertNull(uptoTenValue.errorMessage())
        uptoTenValue.value = 10
        assertNull(uptoTenValue.errorMessage())
        uptoTenValue.value = 11
        assertEquals("Cannot be more than 10", uptoTenValue.errorMessage())
    }

    @Test
    fun check_fromOne() {
        fromOneValue.value = 0
        assertEquals("Cannot be less than 1", fromOneValue.errorMessage())
        fromOneValue.value = 1
        assertNull(fromOneValue.errorMessage())
        fromOneValue.value = 11
        assertNull(fromOneValue.errorMessage())
    }

    fun expectParameterException(body: () -> Unit) {
        try {
            body()
            assertNull("Expected a ParameterException to be thrown")
        } catch (e: ParameterException) {
        }
    }

    @Test
    fun setStringValueExceptions() {
        fromOneValue.value = 5

        expectParameterException({ fromOneValue.stringValue = "a" })

        expectParameterException({ fromOneValue.stringValue = "0a" })
        expectParameterException({ fromOneValue.stringValue = "0a0" })
        expectParameterException({ fromOneValue.stringValue = "1.0" })
        expectParameterException({ fromOneValue.stringValue = "-1a" })

        assertEquals(5, fromOneValue.value)
        assertEquals("5", fromOneValue.stringValue)
    }
}