package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class IntParameterTest {

    val optional = IntParameter("optional", range = 1..10, required = false)

    val oneToTen = IntParameter("oneToTen", range = 1..10)

    val uptoTen = IntParameter("oneToTen", range = Int.MIN_VALUE..10)

    val fromOne = IntParameter("oneToTen", range = 1..Int.MAX_VALUE)

    @Test
    fun setBlankStringValue() {
        optional.value = 1
        assertEquals(1, optional.value)

        optional.stringValue = ""
        assertNull(optional.value)

        optional.value = 2
        assertEquals(2, optional.value)
    }

    @Test
    fun setStringValue() {
        oneToTen.value = 1
        assertEquals(1, oneToTen.value)
        oneToTen.stringValue = "4"
        assertEquals(4, oneToTen.value)
        oneToTen.stringValue = ""
        assertNull(oneToTen.value)
    }

    @Test
    fun getStringValue() {
        oneToTen.value = 1
        assertEquals("1", oneToTen.stringValue)
        oneToTen.stringValue = "4"
        assertEquals("4", oneToTen.stringValue)
        optional.value = null
        assertEquals("", optional.stringValue)
    }

    @Test
    fun check_1To10() {
        oneToTen.value = 1
        assertNull(oneToTen.errorMessage())
        oneToTen.value = 10
        assertNull(oneToTen.errorMessage())
        oneToTen.value = 11
        assertEquals("Must be in the range 1..10", oneToTen.errorMessage())
        oneToTen.value = 0
        assertEquals("Must be in the range 1..10", oneToTen.errorMessage())
    }

    @Test
    fun check_upto10() {
        uptoTen.value = -1
        assertNull(uptoTen.errorMessage())
        uptoTen.value = 10
        assertNull(uptoTen.errorMessage())
        uptoTen.value = 11
        assertEquals("Cannot be more than 10", uptoTen.errorMessage())
    }

    @Test
    fun check_fromOne() {
        fromOne.value = 0
        assertEquals("Cannot be less than 1", fromOne.errorMessage())
        fromOne.value = 1
        assertNull(fromOne.errorMessage())
        fromOne.value = 11
        assertNull(fromOne.errorMessage())
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
        fromOne.value = 5

        expectParameterException({ fromOne.stringValue = "a" })

        expectParameterException({ fromOne.stringValue = "0a" })
        expectParameterException({ fromOne.stringValue = "0a0" })
        expectParameterException({ fromOne.stringValue = "1.0" })
        expectParameterException({ fromOne.stringValue = "-1a" })

        assertEquals(5, fromOne.value)
        assertEquals("5", fromOne.stringValue)
    }
}