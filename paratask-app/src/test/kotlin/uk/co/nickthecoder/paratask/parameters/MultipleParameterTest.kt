package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MultipleParameterTest : ParameterTestBase() {

    @Test
    fun intValues() {
        val multipleP = MultipleParameter<Int?>("multiple") { IntParameter("int") }

        assertEquals("", multipleP.stringValue)
        multipleP.addValue(1)

        assertEquals("1\n", multipleP.stringValue)

        multipleP.addValue(3)
        assertEquals("1\n3\n", multipleP.stringValue)
    }

    @Test
    fun canBeEmpty() {
        val multipleP = MultipleParameter<Int?>("multiple") { IntParameter("int") }
        assertNull(multipleP.errorMessage())
        multipleP.check()
    }

    @Test
    fun needAtLeaseOne() {
        val multipleP = MultipleParameter<Int?>("multiple", minItems = 1) { IntParameter("int") }
        assertEquals("Must have at least 1 items", multipleP.errorMessage())
        expectParameterException { multipleP.check() }
    }

    @Test
    fun maxOf2() {
        val multipleP = MultipleParameter<Int?>("multiple", maxItems = 2) { IntParameter("int") }
        assertNull(multipleP.errorMessage())
        multipleP.check()

        multipleP.stringValue = "1\n3\n5\n"
        assertEquals("Cannot have more than 2 items", multipleP.errorMessage())
        expectParameterException { multipleP.check() }
    }

    @Test
    fun compoundValues() {
        val multipleP = MultipleParameter<CompoundParameter>("multiple") {
            val cp = CompoundParameter("compound")
            val intP = IntParameter("int")
            val stringP = StringParameter("string")
            cp.addParameters(intP, stringP)
            cp
        }
        assertEquals(0, multipleP.value.size)

        assertEquals("", multipleP.stringValue)
        multipleP.stringValue = "int=1\\nstring=hello\n"

        assertEquals(1, multipleP.value.size)

        val first = multipleP.value[0]
        val intP = first.find("int") as IntParameter
        val stringP = first.find("string") as StringParameter

        assertEquals(1, intP.value)
        assertEquals("hello", stringP.value)

        multipleP.stringValue = "int=1\\nstring=hello\nint=3\\nstring=world\n"

        assertEquals(2, multipleP.value.size)
        val second = multipleP.value[1]
        val intP2 = second.find("int") as IntParameter
        val stringP2 = second.find("string") as StringParameter

        assertEquals(3, intP2.value)
        assertEquals("world", stringP2.value)

    }
}
