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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.options.FileOptions
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column

class IncludesTool : AbstractTableTool<String>() {

    override val taskD = TaskDescription("includes", description = "Work with Included Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory")

    override val resultsName = "Includes"

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    override fun createColumns() {
        columns.add(Column<String, String>("include") { it })
    }

    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

    override fun run() {
        list.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

        for (include in optionsFile.listIncludes()) {
            list.add(include)
        }
    }

    override fun updateResults() {
        super.updateResults()
    }

    fun taskEditIncludes(): EditIncludesTask {
        return EditIncludesTask(getFileOptions())
    }

    fun removeInclude(include: String) {
        val fileOptions = getFileOptions()

        fileOptions.includes.remove(include)
        fileOptions.save()
    }


    class EditIncludesTask(val fileOptions: FileOptions)
        : AbstractTask() {

        override val taskD = TaskDescription("Edit Includes")

        val includes = MultipleParameter("includes") { StringParameter("") }

        init {
            taskD.addParameters(includes)
            includes.value = fileOptions.includes
        }

        override fun run() {
            fileOptions.includes.clear()
            fileOptions.includes.addAll(includes.value)
            fileOptions.save()
        }
    }
}
