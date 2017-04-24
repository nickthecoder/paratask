package uk.co.nickthecoder.paratask.parameter

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class GroupParameterTest : ValueListener {

    var group = GroupParameter("group")
    var subGroup = GroupParameter("subGroup")
    val abc = IntParameter("abc")
    val def = IntParameter("def")
    val hij = StringParameter("hij")
    val xyz = IntParameter("xyz")

    var count = 0 // Count the number of times the listener is notified.
    var changedValue: Value<*>? = null

    init {
        group.addParameters(abc, def, subGroup)
        subGroup.add(hij)
    }


    lateinit var groupValue: Values
    //var subGroupValue = subGroup.createValue()
    lateinit var abcValue: IntValue
    lateinit var defValue: IntValue
    lateinit var hijValue: StringValue

    @Before
    fun setUp() {
        count = 0
        changedValue = null

        val values = group.createValue()

        groupValue = values
        //subGroupValue = values.get("subGroup") as Values
        abcValue = values.get("abc") as IntValue
        defValue = values.get("def") as IntValue
        hijValue = values.get("hij") as StringValue

        groupValue.valueListeners.add(this)

    }

    @After
    fun tearDown() {
        groupValue.valueListeners.remove(this)
    }

    override fun valueChanged(value: Value<*>) {
        changedValue = value
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
        assertNull(changedValue)

        abcValue.value = 5
        assertEquals(1, count)
        assertSame(abc, changedValue!!.parameter)
        abcValue.value = 5 // Same value
        assertEquals(1, count) // so the count shouldn't change

        defValue.value = 10
        assertEquals(2, count)
        assertSame(def, changedValue!!.parameter)

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
}