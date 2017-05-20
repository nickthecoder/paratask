package uk.co.nickthecoder.paratask.util

import java.io.File
import java.io.FileFilter
import java.io.IOException

/**
 * Lists files recursively through directory tree structure.
 * Each directory is sorted by the given fileComparator, which defaults to sorting by file name case insensitively.
 *
 * Note. If you wish to sort the whole list, rather than individual sub-directories, then set fileComparator to null,
 * and sort the resulting list yourself afterwards.
 */
class FileLister(
        val depth: Int = 1,
        val onlyFiles: Boolean? = true, // true for files, false for directories, null for either
        val extensions: List<String>? = null,
        val fileComparator: (Comparator<File>)? = CASE_INSENSITIVE,
        val directoryComparator: (Comparator<File>)? = fileComparator,
        val includeHidden: Boolean = false,
        val enterHidden: Boolean = includeHidden,
        val includeBase: Boolean = false,
        val errorHandler: (Exception) -> Unit = { throw(it) }
) : Stoppable, FileFilter {

    companion object {

        fun matchesExtensions(file: File, extensions: List<String>): Boolean {
            val lastDot = file.getName().lastIndexOf('.')
            if (lastDot < 0) {
                return false
            }
            val fe = file.getName().substring(lastDot + 1)
            for (allowed in extensions) {
                if (fe.equals(allowed)) {
                    return true
                }
            }
            return false
        }

        val CASE_SENSITIVE: Comparator<File> = object : Comparator<File> {
            override fun compare(a: File, b: File): Int {
                return a.getPath().toLowerCase().compareTo(b.getPath().toLowerCase());
            }
        }

        /**
         * Compares files based on how their path name strings compare. Note this is case sensitive, and is therefore
         * not usually the best solution. Consider {@link #NAME_ORDER} instead.
         */
        val CASE_INSENSITIVE: Comparator<File> = object : Comparator<File> {
            override fun compare(a: File, b: File): Int {
                return a.getPath().compareTo(b.getPath());
            }
        }
        val SIZE_ORDER = object : Comparator<File> {
            override fun compare(a: File, b: File): Int {
                return a.length().compareTo(b.length())
            }
        }
        val MODIFIED_ORDER = object : Comparator<File> {
            override fun compare(a: File, b: File): Int {
                return a.lastModified().compareTo(b.lastModified())
            }
        }
    }

    var stopping: Boolean = false

    override fun stop() {
        stopping = true
    }

    fun listFiles(directory: File): List<File> {

        stopping = false

        val result = mutableListOf<File>()
        if (includeBase) result.add(directory)

        if (!directory.exists() || !directory.isDirectory) {
            return result
        }

        fun listSingle(directory: File, level: Int) {
            if (stopping || depth < level) return

            val filesAndDirectories: Array<File>
            try {
                filesAndDirectories = directory.listFiles(this)
                if (filesAndDirectories == null) {
                    throw IOException("Failed to list directory ${directory}")
                }

                val files = filesAndDirectories.filter { !it.isDirectory() }
                val sortedFiles = if (fileComparator == null) files else files.sortedWith<File>(fileComparator)

                val directories = filesAndDirectories.filter { it.isDirectory() }
                val sortedDirectories = if (directoryComparator == null)
                    directories
                else
                    directories.sortedWith<File>(directoryComparator)

                for (subDirectory in sortedDirectories) {
                    if (onlyFiles != true && (includeHidden || !subDirectory.isHidden)) {
                        result.add(subDirectory)
                    }
                    listSingle(subDirectory, level + 1)
                }
                for (file in sortedFiles) {
                    if (onlyFiles != false) {
                        result.add(file)
                    }
                }

            } catch (e: Exception) {
                errorHandler(e)
            }
        }

        listSingle(directory, level = 1)
        return result

    }

    override fun accept(file: File): Boolean {

        val isDirectory = file.isDirectory()

        if (onlyFiles == false && !isDirectory) {
            return false
        }
        if (onlyFiles == true && isDirectory) {
            return false
        }

        if (isDirectory) {
            if (!enterHidden && !includeHidden && file.isHidden()) {
                return false
            }

        } else {
            if (!includeHidden && file.isHidden()) {
                return false
            }

        }

        if (extensions != null && extensions.size > 0) {
            if (!matchesExtensions(file, extensions)) {
                return false
            }
        }

        return true
    }

}