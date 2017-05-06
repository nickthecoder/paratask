package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.GroovyOption
import uk.co.nickthecoder.paratask.project.option.Option
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column

class OptionsTool : AbstractTool {

    private val results = mutableListOf<Option>()

    override val taskD = TaskDescription("options", description = "Work with Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory")

    constructor() : super() {}

    constructor(fileOptions: FileOptions) : super() {
        optionsNameP.value = fileOptions.name
        directoryP.value = fileOptions.file.getParentFile()
        println("OptionsTool directory = ${directoryP.stringValue}, ${fileOptions.file}")
    }

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    override fun run() {
        results.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

        for ((_, option) in optionsFile.optionsMap) {
            results.add(option)
        }

    }


    override fun updateResults() {
        toolPane?.updateResults(OptionsResults(this))
    }

    class OptionsResults(tool: OptionsTool) : AbstractTableResults<Option>(tool, tool.results) {

        init {
            columns.add(Column<Option, String>("code") { it.code })
            columns.add(Column<Option, String>("label") { it.label })
            columns.add(Column<Option, Boolean>("isRow") { it.isRow })
            columns.add(Column<Option, Boolean>("isMultiple") { it.isMultiple })
            columns.add(Column<Option, Boolean>("refresh") { it.refresh })
            columns.add(Column<Option, Boolean>("newTab") { it.newTab })
            columns.add(Column<Option, Boolean>("prompt") { it.prompt })
            columns.add(Column<Option, String>("script") { if (it is GroovyOption) it.script else "" })
        }
    }
}

//fun main(args: Array<String>) {
//    CommandLineTool(OptionsTool()).go(args)
//}
