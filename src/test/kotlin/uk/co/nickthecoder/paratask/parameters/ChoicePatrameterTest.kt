package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChoicePatrameterTest : ParameterTestBase() {

    @Test
    fun required() {
        val choiceP = ChoiceParameter<Int?>("int", value = null)

        assertNull(choiceP.value)
        assertEquals("Required", choiceP.errorMessage())

        choiceP.choice("1", 1, "One")
        choiceP.choice("2", 2, "Two")

        choiceP.value = 2

        assertNull(choiceP.errorMessage())

        choiceP.value = 3
        assertEquals("Invalid choice", choiceP.errorMessage())
    }

    @Test
    fun optional() {
        val choiceP = ChoiceParameter<Int?>("int", value = null, required = false)

        assertNull(choiceP.errorMessage())
        choiceP.choice("1", 1, "One")
        choiceP.choice("2", 2, "Two")

        choiceP.value = 2

        assertNull(choiceP.errorMessage())

        choiceP.value = 3
        assertEquals("Invalid choice", choiceP.errorMessage())
    }
}
