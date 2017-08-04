package uk.co.nickthecoder.paratask.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class ResourceTest {

    val base = currentDirectory.child("src", "test", "resources", "Resource")

    @Test
    fun fileTest() {
        val file = File(base, "file")
        val fileResource = Resource(file)
        assert(fileResource.isFile())
        assert(!fileResource.isDirectory())
        assert(fileResource.isFileOrDirectory())

        assertEquals("file", fileResource.name)
        assertEquals("file", fileResource.nameWithoutExtension)

        assertEquals(file.path, fileResource.path)
        assertEquals(file.parentFile, fileResource.parentFile)
        assertEquals(Resource(file.parentFile), fileResource.parentResource())

        assertEquals(file.toURI().toURL().toString(), fileResource.toString())

        assertEquals(file, Resource.toFile(file.toURI().toURL()))
        assertEquals(file.toURI().toURL(), Resource.toURL(file))
        assertEquals(file.toString(), Resource.converter.toString(fileResource))
        assertEquals(fileResource, Resource.converter.fromString(file.toString()))

        val otherResource = Resource(File(base, "other.txt"))
        assertEquals("other.txt", otherResource.name)
        assertEquals("other", otherResource.nameWithoutExtension)
    }

    @Test
    fun directoryTest() {
        val directory = File(base, "directory")
        val directoryResource = Resource(directory)
        assert(!directoryResource.isFile())
        assert(directoryResource.isDirectory())
        assert(directoryResource.isFileOrDirectory())

        assertEquals("directory", directoryResource.name)

        assertEquals(directory.path, directoryResource.path)
        assertEquals(directory.parentFile, directoryResource.parentFile)
        assertEquals(Resource(directory.parentFile), directoryResource.parentResource())

        assertEquals(directory.toURI().toURL().toString(), directoryResource.toString())

        assertEquals(directory, Resource.toFile(directory.toURI().toURL()))
        assertEquals(directory.toString(), Resource.converter.toString(directoryResource))
        assertEquals(directoryResource, Resource.converter.fromString(directory.toString()))

    }

    @Test
    fun nullTest() {
        assertNull(Resource.converter.fromString(""))
        assertNull(Resource.converter.fromString(null))

        assertEquals("", Resource.converter.toString(null))
    }
}
