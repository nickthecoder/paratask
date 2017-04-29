package uk.co.nickthecoder.paratask.parameter

import org.junit.Assert.assertNull
import uk.co.nickthecoder.paratask.ParameterException

open class MyTest {

    fun expectParameterException(body: () -> Unit) {
        try {
            body()
            assertNull("Expected a ParameterException to be thrown")
        } catch (e: ParameterException) {
        }
    }
}