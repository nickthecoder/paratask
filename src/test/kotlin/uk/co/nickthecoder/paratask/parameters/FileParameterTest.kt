package uk.co.nickthecoder.paratask.parameters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.currentDirectory
import java.io.File

class FileParameterTest : ParameterTestBase() {

    val base = currentDirectory.child("src", "test", "resources", "FileParameter")

    @Test
    fun file() {
        val fileP = FileParameter("file")

        assertEquals("Required", fileP.errorMessage())

        fileP.value = File(base, "file1.txt")
        assertNull(fileP.errorMessage())

        fileP.value = File(base, "doesNotExist")
        assertEquals("File does not exist", fileP.errorMessage())

        fileP.value = File(base, "dir1")
        assertEquals("Expeceted a file, but is a directory", fileP.errorMessage())
    }

    @Test
    fun directory() {
        val fileP = FileParameter("file", expectFile = false, value = null)

        assertEquals("Required", fileP.errorMessage())

        fileP.value = File(base, "file1.txt")
        assertEquals("Expeceted a directory, but is a file", fileP.errorMessage())

        fileP.value = File(base, "doesNotExist")
        assertEquals("File does not exist", fileP.errorMessage())

        fileP.value = File(base, "dir1")
        assertNull(fileP.errorMessage())
    }

    @Test
    fun fileOrDirectory() {
        val fileP = FileParameter("file", expectFile = null)

        assertEquals("Required", fileP.errorMessage())

        fileP.value = File(base, "file1.txt")
        assertNull(fileP.errorMessage())

        fileP.value = File(base, "doesNotExist")
        assertEquals("File does not exist", fileP.errorMessage())

        fileP.value = File(base, "dir1")
        assertNull(fileP.errorMessage())
    }

    @Test
    fun mustNotExist() {
        val fileP = FileParameter("file", mustExist = false)

        assertEquals("Required", fileP.errorMessage())

        fileP.value = File(base, "file1.txt")
        assertEquals("File already exists", fileP.errorMessage())

        fileP.value = File(base, "doesNotExist")
        assertNull(fileP.errorMessage())

    }

    @Test
    fun mayExist() {
        val fileP = FileParameter("file", mustExist = null)

        assertEquals("Required", fileP.errorMessage())

        fileP.value = File(base, "file1.txt")
        assertNull(fileP.errorMessage())

        fileP.value = File(base, "doesNotExist")
        assertNull(fileP.errorMessage())
    }

    @Test
    fun extensions() {
        val fileP = FileParameter("file", mustExist = null, extensions = listOf("txt"))

        fileP.value = File(base, "file1.txt")
        assertNull(fileP.errorMessage())

        fileP.value = File(base, "file2.html")
        assertEquals("Incorrect file extension. Expected : [txt]", fileP.errorMessage())
    }

    @Test
    fun stringValue() {
        val fileP = FileParameter("file")

        assertEquals("", fileP.stringValue)

        fileP.stringValue = base.toString()
        assertEquals(base, fileP.value)
        assertEquals(base.toString(), fileP.stringValue)

        val file = File(base, "file1.txt")
        fileP.stringValue = file.toString()
        assertEquals(file, fileP.value)
        assertEquals(file.toString(), fileP.stringValue)
    }

}
