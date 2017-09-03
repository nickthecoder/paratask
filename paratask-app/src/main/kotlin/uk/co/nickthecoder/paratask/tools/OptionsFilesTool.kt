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
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.options.FileOptions
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasDirectory
import java.io.File

class OptionsFilesTool : ListTableTool<FileOptions>(), HasDirectory {

    override val taskD = TaskDescription("optionsFiles", description = "Work with Option Files (does not include those in the jar file)")

    val directoryP = Preferences.createOptionsFileParameter()

    override val directory: File?
        get() = directoryP.value ?: directoryP.choiceValues().filterNotNull().firstOrNull()


    init {
        taskD.addParameters(directoryP)
    }

    override fun createColumns(): List<Column<FileOptions, *>> {
        val columns = mutableListOf<Column<FileOptions, *>>()

        columns.add(Column<FileOptions, String>("name") { fileOptions -> fileOptions.file.nameWithoutExtension })
        columns.add(Column<FileOptions, String>("path") { fileOptions -> fileOptions.file.path })

        return columns
    }

    override fun createHeader() = Header(this, directoryP)

    override fun run() {
        list.clear()
        val chosenDirectory = directoryP.value
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
        files.map { it.nameWithoutExtension }.forEach {
            list.add(OptionsManager.getFileOptions(it, directory))
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(OptionsFilesTool()).go(args)
}
