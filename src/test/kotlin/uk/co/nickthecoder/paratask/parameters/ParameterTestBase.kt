package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertNull
import uk.co.nickthecoder.paratask.ParameterException

open class ParameterTestBase {

    fun expectParameterException(body: () -> Unit) {
        try {
            body()
            assertNull("Expected a ParameterException to be thrown")
        } catch (e: ParameterException) {
        }
    }
}
