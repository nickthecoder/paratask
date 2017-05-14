package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column

class IncludesTool() : AbstractTableTool<String>() {

    override val taskD = TaskDescription("includes", description = "Work with Included Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory")

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    override fun createColumns() {
        columns.add(Column<String, String>("include") { it })
    }

    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

    override fun run() {
        list.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

        for (include in optionsFile.listIncludes()) {
            list.add(include)
        }
    }

    override fun updateResults() {
        println("Updating IncludesTool ${resultsList}")
        super.updateResults()
        println("Updated IncludesTool ${resultsList}")
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

        val includes = MultipleParameter("includes") { StringParameter.factory() }

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
