package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.ParameterException

class IntParameterTest {

	val optional = IntParameter("optional", range = 1..10, required = false)

	val oneToTen = IntParameter("oneToTen", range = 1..10)

	val uptoTen = IntParameter("oneToTen", range = Int.MIN_VALUE..10)

	val fromOne = IntParameter("oneToTen", range = 1..Int.MAX_VALUE)

	@Test
	fun setNullStringValue() {
		optional.value = 1
		assertEquals(1, optional.value)
		optional.setStringValue("")
		assertNull(optional.value)
		optional.value = 2
		assertEquals(2, optional.value)
	}

	@Test
	fun setStringValue() {
		oneToTen.value = 1
		assertEquals(1, oneToTen.value)
		oneToTen.setStringValue("4")
		assertEquals(4, oneToTen.value)
		oneToTen.value = 2
		assertEquals(2, oneToTen.value)
	}

	@Test
	fun getStringValue() {
		oneToTen.value = 1
		assertEquals("1", oneToTen.getStringValue())
		oneToTen.setStringValue("4")
		assertEquals("4", oneToTen.getStringValue())
	}

	@Test
	fun check_1To10() {
		oneToTen.value = 1
		assertNull(oneToTen.errorMesssage())
		oneToTen.value = 10
		assertNull(oneToTen.errorMesssage())
		oneToTen.value = 11
		assertEquals("Must be in the range 1..10", oneToTen.errorMesssage())
		oneToTen.value = 0
		assertEquals("Must be in the range 1..10", oneToTen.errorMesssage())
	}

	@Test
	fun check_upto10() {
		uptoTen.value = -1
		assertNull(uptoTen.errorMesssage())
		uptoTen.value = 10
		assertNull(uptoTen.errorMesssage())
		uptoTen.value = 11
		assertEquals("Cannot be more than 10", uptoTen.errorMesssage())
	}

	@Test
	fun check_fromOne() {
		fromOne.value = 0
		assertEquals("Cannot be less than 1", fromOne.errorMesssage())
		fromOne.value = 1
		assertNull(fromOne.errorMesssage())
		fromOne.value = 11
		assertNull(fromOne.errorMesssage())
	}

	fun expectParameterException(body: () -> Unit) {
		try {
			body()
			assertNull("Expected a ParameterException to be thrown")
		} catch (e: ParameterException) {
		}
	}

	@Test
	fun setStringValueExceptions() {
		fromOne.value = 5

		expectParameterException({ fromOne.setStringValue("a") })

		expectParameterException({ fromOne.setStringValue("0a") })
		expectParameterException({ fromOne.setStringValue("0a0") })
		expectParameterException({ fromOne.setStringValue("1.0") })
		expectParameterException({ fromOne.setStringValue("-1a") })
		assertEquals(5, fromOne.value)
		assertEquals("5", fromOne.getStringValue())
	}
}