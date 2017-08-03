/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
            if (file.isDirectory) {
                // We do NOT exclude directories based on file extensions.
                return true
            }
            val lastDot = file.name.lastIndexOf('.')
            if (lastDot < 0) {
                return false
            }
            val fe = file.name.substring(lastDot + 1)
            return extensions.contains(fe)
        }

        val CASE_INSENSITIVE: Comparator<File> = Comparator { a, b -> a.path.toLowerCase().compareTo(b.path.toLowerCase()); }

        /**
         * Compares files based on how their path name strings compare.
         */
        val CASE_SENSITIVE: Comparator<File> = Comparator { a, b -> a.path.compareTo(b.path); }
        val SIZE_ORDER = Comparator<File> { a, b -> a.length().compareTo(b.length()) }
        val MODIFIED_ORDER = Comparator<File> { a, b -> a.lastModified().compareTo(b.lastModified()) }
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

        fun listSingle(dir: File, level: Int) {
            if (stopping || depth < level) return

            val filesAndDirectories: Array<File>
            try {
                filesAndDirectories = dir.listFiles(this)
                if (filesAndDirectories == null) {
                    throw IOException("Failed to list directory $dir")
                }

                val files = filesAndDirectories.filter { !it.isDirectory }
                val sortedFiles = if (fileComparator == null) files else files.sortedWith<File>(fileComparator)

                val directories = filesAndDirectories.filter { it.isDirectory }
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
        val isDirectory = file.isDirectory

        if (onlyFiles == false && !isDirectory) {
            return false
        }

        if (isDirectory) {
            if (!enterHidden && !includeHidden && file.isHidden) {
                return false
            }

        } else {
            if (!includeHidden && file.isHidden) {
                return false
            }
            if (extensions != null && extensions.isNotEmpty()) {
                if (!matchesExtensions(file, extensions)) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * To make it slightly easier to use FileLister from Groovy (and Java)
     */
    class Builder(
            var depth: Int = 1,
            var onlyFiles: Boolean? = true, // true for files, false for directories, null for either
            var extensions: List<String>? = null,
            var fileComparator: (Comparator<File>)? = CASE_INSENSITIVE,
            var directoryComparator: (Comparator<File>)? = fileComparator,
            var includeHidden: Boolean = false,
            var enterHidden: Boolean = includeHidden,
            var includeBase: Boolean = false,
            var errorHandler: (Exception) -> Unit = { throw(it) }
    ) {

        fun build() = FileLister(
                depth = depth,
                onlyFiles = onlyFiles,
                extensions = extensions,
                fileComparator = fileComparator,
                directoryComparator = directoryComparator,
                includeHidden = includeHidden,
                includeBase = includeBase,
                errorHandler = errorHandler)
    }

}