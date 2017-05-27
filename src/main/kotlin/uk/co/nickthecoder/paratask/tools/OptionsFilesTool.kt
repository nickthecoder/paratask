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
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.nameWithoutExtension

class OptionsFilesTool : AbstractTableTool<FileOptions>() {

    override val taskD = TaskDescription("optionsFiles", description = "Work with Option Files (does not include those in the jar file)")

    val directoryP = Preferences.createOptionsResourceParameter(onlyDirectories = true)

    var directory by directoryP

    init {
        taskD.addParameters(directoryP)
    }

    override fun createColumns() {
        columns.add(Column<FileOptions, String>("name") { fileOptions -> fileOptions.resource.nameWithoutExtension })
        columns.add(Column<FileOptions, String>("path") { fileOptions -> fileOptions.resource.path })
    }

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

    private fun add(resourceDirectory: Resource) {
        val directory = resourceDirectory.file ?: return
        val fileLister = FileLister(extensions = listOf("json"))
        val files = fileLister.listFiles(directory)
        files.map { it.nameWithoutExtension() }.forEach {
            list.add(OptionsManager.getFileOptions(it, resourceDirectory))
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(OptionsFilesTool()).go(args)
}
