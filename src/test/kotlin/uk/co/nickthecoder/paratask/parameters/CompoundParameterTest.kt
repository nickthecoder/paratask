package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class CompoundParameterTest {

    @Test
    fun intAndString() {
        val intP = IntParameter("int")
        val stringP = StringParameter("string")
        val compoundP = CompoundParameter("both")
        compoundP.addParameters(intP, stringP)

        intP.value = 1
        stringP.value = "hello"

        assertEquals("int=1\nstring=hello", compoundP.stringValue)

        compoundP.stringValue = "int=2\nstring=world"
        assertEquals("2", intP.stringValue)
        assertEquals("world", stringP.value)

        val copyP = compoundP.copy()
        assertEquals("int=2\nstring=world", copyP.stringValue)

        val copyIntP = copyP.find("int") as IntParameter
        val copyStringP = copyP.find("string") as StringParameter

        assertTrue(copyIntP !== intP)
        assertTrue(copyStringP !== stringP)

        assertEquals(2, copyIntP.value)
        assertEquals("world", copyStringP.value)
    }
}
