package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.GroovyOption
import uk.co.nickthecoder.paratask.project.option.Option
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


    override fun updateResults() {
        toolPane?.updateResults(IncludesResults(this))
    }

    fun editTask(option: Option): EditOption {
        return EditOption(getFileOptions(), option)
    }

    fun newTask(): NewOption {
        return NewOption(getFileOptions())
    }

    class IncludesResults(tool: IncludesTool) : AbstractTableResults<String>(tool, tool.results) {

        init {
            columns.add(Column<String, String>("include") { it })
        }
    }
}

open class EditInclude(val fileOptions: FileOptions, val option: Option, name: String = "editOption") : SimpleTask() {

    override val taskD = TaskDescription(name)

    val code = StringParameter("code", value = option.code)

    val label = StringParameter("label", value = option.label)

    var isRow = BooleanParameter("isRow", value = option.isRow)

    var isMultiple = BooleanParameter("isMultiple", value = option.isMultiple)

    var refresh = BooleanParameter("refresh", value = option.refresh)

    var newTab = BooleanParameter("newTab", value = option.newTab)

    var prompt = BooleanParameter("prompt", value = option.prompt)

    var script = StringParameter("script", value = option.script)

    init {
        taskD.addParameters(code, label, isRow, isMultiple, refresh, newTab, prompt, script)
    }

    override fun run() {
        update()
        addOrRename()
        save()
    }

    open fun addOrRename() {
        fileOptions.renameOption(option, code.value)
    }

    open fun update() {

        option.label = label.value
        option.isRow = isRow.value == true
        option.isMultiple = isMultiple.value == true
        option.refresh = refresh.value == true
        option.newTab = newTab.value == true
        option.prompt = prompt.value == true
        option.script = script.value
    }

    open fun save() {
        fileOptions.save()
    }
}

open class NewInclude(fileOptions: FileOptions) : EditOption(fileOptions, GroovyOption(""), name = "newOption") {

    override open fun addOrRename() {
        fileOptions.addOption(option)
    }
}

open class DeleteInclude(val fileOptions: FileOptions, val option: Option) : SimpleTask() {

    override val taskD = TaskDescription("deleteOption", description = "Delete option : ${option.code} - ${option.label}")


    override fun run() {
        fileOptions.removeOption(option)
        fileOptions.save()

    }
}


//fun main(args: Array<String>) {
//    CommandLineTool(OptionsTool()).go(args)
//}
