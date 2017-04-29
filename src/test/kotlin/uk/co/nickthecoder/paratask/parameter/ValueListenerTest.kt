package uk.co.nickthecoder.paratask.parameter

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ValueListenerTest : ValueListener {
    var count = 0

    var notListening = IntParameter("notListening")
    var listening = IntParameter("listening")

        var notListeningValue = notListening.createValue()
    var listeningValue = listening.createValue()

    override fun valueChanged(parameterValue: ParameterValue<*>) {
        count++
    }

    @Before
    fun setUp() {
        count = 0
        notListeningValue = notListening.createValue()
        listeningValue = listening.createValue()

        listeningValue.valueListeners.add(this)
    }

    @After
    fun tearDown() {
        listeningValue.valueListeners.remove(this)
    }

    @Test
    fun noListner() {
        notListeningValue.value = 1
        assertEquals(0, count)
    }

    @Test
    fun singleChange() {
        listeningValue.value = 1
        assertEquals(1, count)
    }

    @Test
    fun doubleChange() {
        listeningValue.value = 1
        listeningValue.value = 0
        assertEquals(2, count)
    }

    @Test
    fun doubleChangeWithRemove() {
        listeningValue.value = 1
        listeningValue.valueListeners.remove(this)
        listeningValue.value = 0
        assertEquals(1, count)
    }

    @Test
    fun sameValue() {
        listeningValue.value = 1
        listeningValue.value = 1
        assertEquals(1, count)
    }
}

