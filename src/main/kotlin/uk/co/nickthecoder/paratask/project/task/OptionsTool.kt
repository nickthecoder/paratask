package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.HasCopyableTasks
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskRegistry
import uk.co.nickthecoder.paratask.gui.project.SharedToolPane
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.OneOfParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.TaskParameter
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.GroovyOption
import uk.co.nickthecoder.paratask.project.option.Option
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.option.TaskOption
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.BooleanColumn
import uk.co.nickthecoder.paratask.project.table.Column

class OptionsTool : AbstractTableTool<Option> {

    override val taskD = TaskDescription("options", description = "Work with Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory", expectFile = false)

    var includesTool: IncludesTool = IncludesTool()

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    override val resultsName = "Options"

    val tool: Tool?

    constructor() : super() {
        tool = null
    }

    constructor(tool: Tool) : super() {
        this.tool = tool
        optionsNameP.value = tool.optionsName
        directoryP.value = Preferences.optionsPath[0]
    }

    constructor(fileOptions: FileOptions) : this() {
        optionsNameP.value = fileOptions.name
        directoryP.value = fileOptions.file.getParentFile()
    }

    constructor(optionsName: String) : this() {
        optionsNameP.value = optionsName
        directoryP.value = Preferences.optionsPath[0]
    }

    override fun createColumns() {
        columns.add(Column<Option, String>("code") { it.code })
        columns.add(Column<Option, String>("label") { it.label })
        columns.add(BooleanColumn<Option>("isRow") { it.isRow })
        columns.add(BooleanColumn<Option>("isMultiple") { it.isMultiple })
        columns.add(BooleanColumn<Option>("refresh") { it.refresh })
        columns.add(BooleanColumn<Option>("newTab") { it.newTab })
        columns.add(BooleanColumn<Option>("prompt") { it.prompt })
        columns.add(Column<Option, String>("script") {
            when (it) {
                is GroovyOption -> it.script
                is TaskOption -> it.task.taskD.name
                else -> ""
            }
        })

    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        includesTool.toolPane = SharedToolPane(this)
    }

    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

    override fun run() {
        shortTitle = "${defaultShortTitle()} (${optionsNameP.value})"
        list.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

        for (option in optionsFile.listOptions()) {
            list.add(option)
        }

        includesTool.optionsNameP.value = optionsNameP.value
        includesTool.directoryP.value = directoryP.value
        includesTool.run()
    }

    override fun updateResults() {
        includesTool.updateResults()
        super.updateResults()
    }

    fun taskEdit(option: Option): EditOptionTask {
        return EditOptionTask(getFileOptions(), option, tool = tool)
    }

    fun taskCopy(option: Option): CopyOptionTask {
        return CopyOptionTask(getFileOptions(), option, tool = tool)
    }

    fun taskNew(): NewOptionTask {
        return NewOptionTask(getFileOptions(), tool = tool)
    }

    fun taskDelete(option: Option): DeleteOptionTask {
        return DeleteOptionTask(getFileOptions(), option)
    }


    open class EditOptionTask(
            val fileOptions: FileOptions,
            val option: Option,
            val tool: Tool?,
            name: String = "editOption")

        : AbstractTask() {

        override val taskD = TaskDescription(name)

        val code = StringParameter("code", value = option.code)

        val aliases = MultipleParameter("aliases", value = option.aliases) { StringParameter("") }

        val label = StringParameter("label", value = option.label)

        var isRow = BooleanParameter("isRow", value = option.isRow)

        var isMultiple = BooleanParameter("isMultiple", value = option.isMultiple)

        var refresh = BooleanParameter("refresh", value = option.refresh)

        var newTab = BooleanParameter("newTab", value = option.newTab)

        var prompt = BooleanParameter("prompt", value = option.prompt)

        val scriptOrTaskP = OneOfParameter("typeOfAction")

        var script = StringParameter("script", value = if (option is GroovyOption) option.script else "")

        val tasks = if (tool is HasCopyableTasks) {
            TaskRegistry.tools + TaskRegistry.tasks + tool.tasks
        } else {
            TaskRegistry.tools + TaskRegistry.tasks
        }

        var taskP = TaskParameter("task", tasks = tasks, value = if (option is TaskOption) option.task else null)

        init {
            if (option is GroovyOption) {
                script.value = option.script
                scriptOrTaskP.value = script
            } else if (option is TaskOption) {
                taskP.value = option.task.copy()
                scriptOrTaskP.value = taskP
            }
            taskD.addParameters(code, aliases, label, isRow, isMultiple, refresh, newTab, prompt, scriptOrTaskP)
            scriptOrTaskP.addParameters(script, taskP)
        }

        override fun run() {
            val option = update()
            save(option)
        }

        open fun update(): Option {

            val newOption: Option
            if (scriptOrTaskP.value == script) {
                newOption = GroovyOption(script.value)
            } else {
                newOption = TaskOption(taskP.value!!.copy())
            }
            newOption.code = code.value
            newOption.aliases = aliases.value.toMutableList()
            newOption.label = label.value
            newOption.isRow = isRow.value == true
            newOption.isMultiple = isMultiple.value == true
            newOption.refresh = refresh.value == true
            newOption.newTab = newTab.value == true
            newOption.prompt = prompt.value == true

            return newOption
        }

        open fun save(newOption: Option) {
            fileOptions.removeOption(option)
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class NewOptionTask(fileOptions: FileOptions, tool: Tool?)
        : EditOptionTask(fileOptions, GroovyOption(""), name = "newOption", tool = tool) {

        override open fun save(newOption: Option) {
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class CopyOptionTask(fileOptions: FileOptions, option: Option, tool: Tool?)
        : EditOptionTask(fileOptions, option.copy(), name = "copyOption", tool = tool) {

        override open fun save(newOption: Option) {
            super.update()
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class DeleteOptionTask(val fileOptions: FileOptions, val option: Option) : AbstractTask() {

        override val taskD = TaskDescription("deleteOption", description = "Delete option : ${option.code} - ${option.label}")

        override fun run() {
            fileOptions.removeOption(option)
            fileOptions.save()
        }
    }
}

//fun main(args: Array<String>) {
//    CommandLineTool(OptionsTool()).go(args)
//}
