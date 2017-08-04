package uk.co.nickthecoder.paratask.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileListerTest {

    val base = currentDirectory.child("src", "test", "resources", "FileLister")

    @Test
    fun listFilesDepth1() {
        val files = FileLister().listFiles(base)
        assertEquals(listOf(File(base, "file1.txt"), File(base, "file2.html"), File(base, "File3.txt"), File(base, "file4")), files)
    }

    @Test
    fun listFilesDepth1CaseSensitive() {
        val files = FileLister(fileComparator = FileLister.CASE_SENSITIVE).listFiles(base)
        assertEquals(listOf(File(base, "File3.txt"), File(base, "file1.txt"), File(base, "file2.html"), File(base, "file4")), files)
    }

    @Test
    fun listFilesDepth1ByDate() {
        val files = FileLister(fileComparator = FileLister.MODIFIED_ORDER).listFiles(base)
        var previousDate: Long = 0
        for (file in files) {
            assertTrue(file.lastModified() >= previousDate)
            previousDate = file.lastModified()
        }
    }

    @Test
    fun listFilesDepth1BySzie() {
        val files = FileLister(depth = 2, onlyFiles = false, fileComparator = FileLister.SIZE_ORDER).listFiles(base)
        var previousSize: Long = 0
        for (file in files) {
            assertTrue(file.length() >= previousSize)
            previousSize = file.length()
        }
    }

    @Test
    fun listFilesDepth1IncludeHidden() {

        val files = FileLister(includeHidden = true).listFiles(base)
        assertEquals(listOf(File(base, ".hidden"), File(base, "file1.txt"), File(base, "file2.html"), File(base, "File3.txt"), File(base, "file4")), files)
    }

    @Test
    fun listDirectoriesDepth1() {
        val files = FileLister(onlyFiles = false).listFiles(base)
        assertEquals(listOf(File(base, "dir1"), File(base, "dir2")), files)
    }

    @Test
    fun listDirectoriesDepth1IncludeBase() {
        val files = FileLister(onlyFiles = false, includeBase = true).listFiles(base)
        assertEquals(listOf(base, File(base, "dir1"), File(base, "dir2")), files)
    }

    @Test
    fun listFilesDepth2() {
        val files = FileLister(depth = 2).listFiles(base)
        assertEquals(listOf(base.child("dir1", "child1a"), base.child("dir1", "child1b"), File(base, "file1.txt"), File(base, "file2.html"), File(base, "File3.txt"), File(base, "file4")), files)
    }

    @Test
    fun listFilesDepth2EnterHidden() {
        val files = FileLister(depth = 2, enterHidden = true).listFiles(base)
        assertEquals(listOf(base.child(".hiddenDir", "inHidden"), base.child("dir1", "child1a"), base.child("dir1", "child1b"), File(base, "file1.txt"), File(base, "file2.html"), File(base, "File3.txt"), File(base, "file4")), files)
    }

    @Test
    fun listTxtFiles() {
        val files = FileLister(depth = 2, extensions = listOf("txt")).listFiles(base)
        assertEquals(listOf(File(base, "file1.txt"), File(base, "File3.txt")), files)
    }

    @Test
    fun listTxtAndHtmlFiles() {
        val files = FileLister(depth = 2, extensions = listOf("txt", "html")).listFiles(base)
        assertEquals(listOf(File(base, "file1.txt"), File(base, "file2.html"), File(base, "File3.txt")), files)
    }
}
