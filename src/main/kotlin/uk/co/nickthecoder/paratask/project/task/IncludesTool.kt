package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column

class IncludesTool() : AbstractTool() {

    private val results = mutableListOf<String>()

    override val taskD = TaskDescription("includes", description = "Work with Included Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory")

    constructor(fileOptions: FileOptions) : this() {
        optionsNameP.value = fileOptions.name
        directoryP.value = fileOptions.file.getParentFile()
    }

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

    override fun run() {
        results.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

        for (include in optionsFile.listIncludes()) {
            results.add(include)
        }
    }

    fun createResults(): IncludesResults = IncludesResults(this)


    override fun updateResults() {
        toolPane?.updateResults(createResults())
    }

    fun taskEditIncludes(): OptionsTool.EditIncludesTask {
        return OptionsTool.EditIncludesTask(getFileOptions())
    }

    fun removeInclude(include: String) {
        val fileOptions = getFileOptions()

        fileOptions.includes.remove(include)
        fileOptions.save()
    }

    class IncludesResults(tool: IncludesTool) : AbstractTableResults<String>(tool, tool.results, "Includes") {

        init {
            columns.add(Column<String, String>("include") { it })
        }
    }
}

//fun main(args: Array<String>) {
//    CommandLineTool(OptionsTool()).go(args)
//}
