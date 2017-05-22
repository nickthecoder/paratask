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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.options.FileOptions
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.File

class OptionsFilesTool : AbstractTableTool<FileOptions>() {

    override val taskD = TaskDescription("optionsFiles", description = "Work with all Option Files")

    val directory = Preferences.createOptionsDirectoryParameter()

    init {
        taskD.addParameters(directory)
    }

    override fun createColumns() {
        columns.add(Column<FileOptions, String>("name") { fileOptions -> fileOptions.file.nameWithoutExtension() })
        columns.add(Column<FileOptions, String>("path") { fileOptions -> fileOptions.file.path })
    }

    override fun run() {
        list.clear()
        val chosenDirectory = directory.value
        if (chosenDirectory == null) {
            for (dir in Preferences.optionsPath) {
                add(dir)
            }
        } else {
            add(chosenDirectory)
        }
    }

    private fun add(directory: File) {
        val fileLister = FileLister(extensions = listOf("json"))
        val files = fileLister.listFiles(directory)
        files.map { it.nameWithoutExtension() }
            .forEach { list.add(OptionsManager.getFileOptions(it, directory)) }
    }
}

fun main(args: Array<String>) {
    ToolParser(OptionsFilesTool()).go(args)
}
