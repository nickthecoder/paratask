package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertSame
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.Before
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription

class ValuesTest {

    val taskD = TaskDescription()
    val intP = IntParameter("int")
    val stringP = StringParameter("string")
    val subGroupP = GroupParameter("subGroup")
    val innerP = StringParameter("inner")

    @Before
    fun startUp() {
        subGroupP.addParameters(innerP)
        taskD.addParameters(intP, stringP, subGroupP)
    }

    @Test
    fun copy() {

        val source = taskD.createValues()
        // Set some default values
        intP.set(source, 5)
        innerP.set(source, "unchanged")

        // Copy the values
        val copy = taskD.copyValues(source)

        // Check all the values are equal, but don't share the same instance
        assertEquals(source.parameterValues.size, copy.parameterValues.size)
        source.parameterValues.forEach { (name, sourceItem) ->

            assertTrue(copy.parameterValues.containsKey(name))
            assertNotNull(copy.get(name))

            val copyItem = copy.get(name)!!

            assertSame(sourceItem.parameter, copyItem.parameter)

            assertNotSame(sourceItem, copyItem)
            assertEquals(sourceItem.value, copyItem.value)
        }

        // Changing the copy does NOT change the original
        (copy.get("int") as IntValue).value = 6
        assertEquals(6, (copy.get("int") as IntValue).value)
        assertEquals(5, (source.get("int") as IntValue).value)

        // Changing the copy does NOT change the original
        (copy.get("inner") as StringValue).value = "changed"
        assertEquals("changed", (copy.get("inner") as StringValue).value)
        assertEquals("unchanged", (source.get("inner") as StringValue).value)

    }
}