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

class IncludesTool() : AbstractTableTool<String>() {

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


    open class EditIncludesTask(val fileOptions: FileOptions)
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
