package uk.co.nickthecoder.paratask.parameter

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class GroupParameterTest : MyTest(), ParameterListener {

    var group = GroupParameter("group")
    var subGroup = GroupParameter("subGroup")
    val abc = IntParameter("abc")
    val def = IntParameter("def")
    val hij = StringParameter("hij")
    val xyz = IntParameter("xyz")

    var count = 0 // Count the number of times the listener is notified.
    var changedParameter: Parameter? = null
    var innerParameter: Parameter? = null

    init {
        group.addParameters(abc, def, subGroup)
        subGroup.add(hij)
    }

    @Before
    fun setUp() {
        count = 0
        changedParameter = null
        innerParameter = null

        group.parameterListeners.add(this)

    }

    @After
    fun tearDown() {
        group.parameterListeners.remove(this)
    }

    override fun parameterChanged(event: ParameterEvent) {
        changedParameter = event.parameter
        innerParameter = event.innerParameter
        count++
    }

    @Test
    fun find() {
        assertSame(abc, group.find("abc"))
        assertSame(def, group.find("def"))
        assertNull(group.find("xyz"))
    }

    @Test
    fun subFind() {
        assertSame(hij, group.find("hij"))
    }

    @Test
    fun childChanged() {
        assertEquals(0, count)
        assertNull(changedParameter)

        abc.value = 5
        assertEquals(1, count)
        assertSame(abc, innerParameter)
        assertSame(group, changedParameter)
        abc.value = 5 // Same value
        assertEquals(1, count) // so the count shouldn't change

        innerParameter = null
        changedParameter = null

        def.value = 10
        assertEquals(2, count)
        assertSame(def, innerParameter)
        assertSame(group, changedParameter)

    }

    @Test
    fun children() {
        assertEquals(listOf(abc, def, subGroup), group.children())
    }

    @Test
    fun descendants() {
        assertEquals(4, group.descendants().size)
        for (i in 0..3) {
            assertEquals(listOf(abc, def, subGroup, hij)[i], group.descendants()[i])
        }
    }

    @Test
    fun duplicateChild() {
        val param1 = IntParameter("one")
        val param2 = StringParameter("one")

        val group = GroupParameter("group")
        group.add(param1)
        expectParameterException { group.add(param2) }
    }

    @Test
    fun duplicateAncestor() {
        val param1 = IntParameter("one")
        val param2 = StringParameter("one")

        val group1 = GroupParameter("group1")
        group1.add(param1)
        val group2 = GroupParameter("group2")
        group2.add(param2)

        expectParameterException { group2.add(param2) }
    }

    @Test
    fun duplicateAncestor2() {
        val param1 = IntParameter("one")
        val param2 = StringParameter("one")

        val group1 = GroupParameter("group1")
        group1.add(param1)

        val group2 = GroupParameter("group2")
        group2.add(param2)

        expectParameterException { group1.add(group2) }
    }

    @Test
    fun duplicateAncestor3() {
        val param1 = IntParameter("one")
        val param2 = StringParameter("one")

        val group1 = GroupParameter("group1")
        group1.add(param1)

        val group2 = GroupParameter("group2")
        group1.add(group2)

        expectParameterException { group2.add(param2) }
    }

    @Test
    fun addToTwice() {
        val group1 = GroupParameter("group1")
        val group2 = GroupParameter("group2")
        val param1 = IntParameter("one")
        group1.add(param1)
        expectParameterException { group1.add(param1) }
        expectParameterException { group2.add(param1) }
    }

    @Test
    fun addToIteself() {
        val group = GroupParameter("group")
        expectParameterException { group.add(group) }
    }

}