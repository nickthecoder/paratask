package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.currentDirectory
import java.io.File

class ResourceParameterTest : ParameterTestBase() {

    val base = currentDirectory.child("src", "test", "resources", "FileParameter")

    @Test
    fun file() {
        val resourceP = ResourceParameter("resource")

        Assert.assertEquals("Required", resourceP.errorMessage())

        resourceP.value = Resource(File(base, "file1.txt"))
        Assert.assertNull(resourceP.errorMessage())

        resourceP.value = Resource(File(base, "doesNotExist"))
        Assert.assertEquals("Must either be a valid URL, or a file/directory that exists", resourceP.errorMessage())

        resourceP.value = Resource(File(base, "dir1"))
        Assert.assertEquals("Expected a file, not a directory", resourceP.errorMessage())
    }

    @Test
    fun resource() {
        val resourceP = ResourceParameter("resource")

        resourceP.value = Resource("http://example.com")
        assertNull(resourceP.errorMessage())
    }
}
