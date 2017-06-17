package uk.co.nickthecoder.paratask.util

import org.junit.Test
import java.io.File

class WrappedFileTest() {

    val base = currentDirectory.child("src", "test", "resources", "WrappedFile")

    @Test
    fun file() {

        val file = WrappedFile(File(base, "file"))
        assert(file.isFile())
        assert(!file.isDirectory())
    }

    @Test
    fun directory() {

        val directory = WrappedFile(File(base, "directory"))
        assert(!directory.isFile())
        assert(directory.isDirectory())
    }
}
