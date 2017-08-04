package uk.co.nickthecoder.paratask.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FileExtensionsTest {

    val base = currentDirectory.child("src", "test", "resources", "FileExtensions")

    @Test
    fun childTest() {
        assertEquals("FileExtensions", base.name)
        assertEquals("resources", base.parentFile.name)
        assertEquals("test", base.parentFile.parentFile.name)
        assertEquals("src", base.parentFile.parentFile.parentFile.name)
        assertEquals(currentDirectory, base.parentFile.parentFile.parentFile.parentFile)
    }
}
