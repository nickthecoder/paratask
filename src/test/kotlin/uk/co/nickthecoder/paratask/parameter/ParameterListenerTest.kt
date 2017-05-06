package uk.co.nickthecoder.paratask.parameter

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ParameterListenerTest : ParameterListener {
    var count = 0

    var notListening = IntParameter("notListening")
    var listening = IntParameter("listening")

    override fun parameterChanged(parameter: Parameter) {
        count++
    }

    @Before
    fun setUp() {
        count = 0
    }

    @After
    fun tearDown() {
        listening.parameterListeners.remove(this)
    }

    @Test
    fun noListner() {
        notListening.value = 1
        assertEquals(0, count)
    }

    @Test
    fun singleChange() {
        listening.value = 1
        assertEquals(1, count)
    }

    @Test
    fun doubleChange() {
        listening.value = 1
        listening.value = 0
        assertEquals(2, count)
    }

    @Test
    fun doubleChangeWithRemove() {
        listening.value = 1
        listening.parameterListeners.remove(this)
        listening.value = 0
        assertEquals(1, count)
    }

    @Test
    fun sameValue() {
        listening.value = 1
        listening.value = 1
        assertEquals(1, count)
    }
}

