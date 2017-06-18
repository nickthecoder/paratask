package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertEquals
import org.junit.Test

class MultipleParameterTest {

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
    }
}
