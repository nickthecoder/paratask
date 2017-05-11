package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.SharedToolPane
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.GroovyOption
import uk.co.nickthecoder.paratask.project.option.Option
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.BooleanColumn
import uk.co.nickthecoder.paratask.project.table.Column

class OptionsTool() : AbstractTool() {

    private val results = mutableListOf<Option>()

    override val taskD = TaskDescription("options", description = "Work with Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = FileParameter("directory", expectFile = false)

    lateinit var includesTool: IncludesTool

    constructor(fileOptions: FileOptions) : this() {
        optionsNameP.value = fileOptions.name
        directoryP.value = fileOptions.file.getParentFile()
    }

    constructor(optionsName: String) : this() {
        optionsNameP.value = optionsName
        directoryP.value = Preferences.optionsPath[0]
    }

    init {
        taskD.addParameters(optionsNameP, directoryP)
    }

    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

    override fun run() {
        results.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.requiredValue())

        for (option in optionsFile.listOptions()) {
            results.add(option)
        }

        includesTool = IncludesTool(optionsFile)
        includesTool.toolPane = SharedToolPane(this)
        includesTool.run()
    }

    override fun updateResults() {
        toolPane?.updateResults(OptionsResults(this), includesTool.createResults())
    }

    fun taskEdit(option: Option): EditOptionTask {
        return EditOptionTask(getFileOptions(), option)
    }

    fun taskNew(): NewOptionTask {
        return NewOptionTask(getFileOptions())
    }

    fun taskDelete(option: Option): DeleteOptionTask {
        return DeleteOptionTask(getFileOptions(), option)
    }

    fun taskEditIncludes(): EditIncludesTask {
        return EditIncludesTask(getFileOptions())
    }

    class OptionsResults(tool: OptionsTool) : AbstractTableResults<Option>(tool, tool.results, "Options") {

        init {
            columns.add(Column<Option, String>("code") { it.code })
            columns.add(Column<Option, String>("label") { it.label })
            columns.add(BooleanColumn<Option>("isRow") { it.isRow })
            columns.add(BooleanColumn<Option>("isMultiple") { it.isMultiple })
            columns.add(BooleanColumn<Option>("refresh") { it.refresh })
            columns.add(BooleanColumn<Option>("newTab") { it.newTab })
            columns.add(BooleanColumn<Option>("prompt") { it.prompt })
            columns.add(Column<Option, String>("script") { if (it is GroovyOption) it.script else "" })
        }
    }


    open class EditOptionTask(val fileOptions: FileOptions, val option: Option, name: String = "editOption")
        : AbstractTask() {

        override val taskD = TaskDescription(name)

        val code = StringParameter("code", value = option.code)

        val aliases = MultipleParameter("aliases", value = option.aliases) { StringParameter.factory() }

        val label = StringParameter("label", value = option.label)

        var isRow = BooleanParameter("isRow", value = option.isRow)

        var isMultiple = BooleanParameter("isMultiple", value = option.isMultiple)

        var refresh = BooleanParameter("refresh", value = option.refresh)

        var newTab = BooleanParameter("newTab", value = option.newTab)

        var prompt = BooleanParameter("prompt", value = option.prompt)

        var script = StringParameter("script", value = option.script)

        init {
            taskD.addParameters(code, aliases, label, isRow, isMultiple, refresh, newTab, prompt, script)
        }

        override fun run() {
            update()
            save()
        }

        open fun update() {

            option.code = code.value
            option.aliases = aliases.value.toMutableList()
            option.label = label.value
            option.isRow = isRow.value == true
            option.isMultiple = isMultiple.value == true
            option.refresh = refresh.value == true
            option.newTab = newTab.value == true
            option.prompt = prompt.value == true
            option.script = script.value
            fileOptions.update(option)
        }

        open fun save() {
            fileOptions.save()
        }
    }

    open class NewOptionTask(fileOptions: FileOptions)
        : EditOptionTask(fileOptions, GroovyOption(""), name = "newOption") {

        override open fun update() {
            super.update()
            fileOptions.addOption(option)
        }
    }

    open class DeleteOptionTask(val fileOptions: FileOptions, val option: Option) : AbstractTask() {

        override val taskD = TaskDescription("deleteOption", description = "Delete option : ${option.code} - ${option.label}")


        override fun run() {
            fileOptions.removeOption(option)
            fileOptions.save()

        }
    }

    open class EditIncludesTask(val fileOptions: FileOptions)
        : AbstractTask() {

        override val taskD = TaskDescription("Edit Includes")

        val includes = MultipleParameter("includes") { StringParameter.factory() }

        init {
            taskD.addParameters(includes)
        }

        override fun run() {
            fileOptions.includes.clear()
            fileOptions.includes.addAll(includes.value)
            fileOptions.save()
        }
    }
}

//fun main(args: Array<String>) {
//    CommandLineTool(OptionsTool()).go(args)
//}
