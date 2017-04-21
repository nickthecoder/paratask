package uk.co.nickthecoder.paratask.parameter

import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class GroupParameterTest : ParameterListener {


    var group = GroupParameter("group")
    val abc = IntParameter("abc")
    val def = IntParameter("def")
    val xyz = IntParameter( "xyz" )
    var count = 0 // Count the number of times the listener is notified.
    var changedParameter : Parameter? = null

    @Before
    fun setUp() {
        count = 0
        group = GroupParameter("group")
        group.addListener(this)
    }

    @After
    fun tearDown() {
        group.removeListener(this)
    }

    override fun parameterChanged(parameter: Parameter) {
        changedParameter = parameter
        count ++
    }

    @Test
    fun find() {
        group.add(abc)
        group.add(def)

        assertEquals(abc, group.find("abc"))
        assertEquals(def, group.find("def"))
        assertNull(group.find("xyz"))
    }

    @Test
    fun addAll() {
        group.add(abc, def)

        assertEquals(abc, group.find("abc"))
        assertEquals(def, group.find("def"))
        assertNull(group.find("xyz"))
    }

    @Test
    fun childChanged() {
        group.add(abc, def)
        assertEquals(0, count)
        assertNull(changedParameter)

        abc.value = 5
        assertEquals(1, count)
        assertSame(abc,changedParameter)
        abc.value = 5 // Same value
        assertEquals(1, count)

        def.value = 10
        assertEquals(2,count)
        assertSame(def,changedParameter)

        xyz.value = 15
        assertEquals(2,count)
    }
}