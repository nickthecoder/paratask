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

import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.RegisteredTaskFactory
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.ScriptVariables
import uk.co.nickthecoder.paratask.gui.SimpleDragHelper
import uk.co.nickthecoder.paratask.misc.AutoRefresh
import uk.co.nickthecoder.paratask.options.*
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import java.io.File

class OptionsTool() : ListTableTool<Option>() {

    override val taskD = TaskDescription("options", description = "Work with Options")

    val optionsNameP = StringParameter("optionsName")

    val directoryP = Preferences.createOptionsFileParameter(required = true)

    var includesTool: IncludesTool = IncludesTool()

    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.run() }

    val file: File
        get() = File(directoryP.value, optionsNameP.value + ".json")

    constructor(tool: Tool) : this() {
        optionsNameP.value = tool.optionsName
        directoryP.value = Preferences.optionsPath[0]
    }

    constructor(fileOptions: FileOptions) : this() {
        optionsNameP.value = fileOptions.name
        directoryP.value = fileOptions.file.parentFile
    }

    constructor(optionsName: String) : this() {
        optionsNameP.value = optionsName
        directoryP.value = Preferences.optionsPath[0]
    }

    init {
        taskD.addParameters(optionsNameP, directoryP)

        columns.add(Column<Option, String>("code", getter = { it.code }))
        columns.add(Column<Option, String>("label", getter = { it.label }))
        columns.add(BooleanColumn<Option>("isRow", getter = { it.isRow }))
        columns.add(BooleanColumn<Option>("isMultiple", getter = { it.isMultiple }))
        columns.add(BooleanColumn<Option>("refresh", getter = { it.refresh }))
        columns.add(BooleanColumn<Option>("newTab", getter = { it.newTab }))
        columns.add(BooleanColumn<Option>("prompt", getter = { it.prompt }))
        columns.add(Column<Option, String>("script", getter = {
            when (it) {
                is GroovyOption -> it.script
                is TaskOption -> it.task.taskD.name
                else -> ""
            }
        }))
    }

    override val rowFilter = RowFilter<Option>(this, columns, GroovyOption())

    override val resultsName = "Options"


    override fun createHeader() = Header(this,
            HeaderRow(directoryP),
            HeaderRow(optionsNameP))

    override fun createTableResults(): TableResults<Option> {
        val tableResults = super.createTableResults()

        tableResults.dragHelper = SimpleDragHelper<List<Option>>(Option.dataFormat, onMoved = { onMoved(it) }) {
            tableResults.selectedRows()
        }

        tableResults.dropHelper = object : TableDropHelper<List<Option>, Option>(Option.dataFormat) {

            override fun acceptDropOnNonRow() = arrayOf(TransferMode.COPY, TransferMode.MOVE)

            override fun acceptDropOnRow(row: Option) = null

            override fun droppedOnRow(row: Option, content: List<Option>, transferMode: TransferMode) {
            }

            override fun droppedOnNonRow(content: List<Option>, transferMode: TransferMode) {
                val fileOptions = getFileOptions()
                for (option in content) {
                    fileOptions.addOption(option.copy())
                }
                fileOptions.save()
            }
        }

        return tableResults
    }

    fun onMoved(content: List<Option>) {
        val fileOptions = getFileOptions()
        content.map { fileOptions.find(it.code) }.filterNotNull().forEach {
            fileOptions.removeOption(it)
        }
        fileOptions.save()
    }

    override fun createResults(): List<Results> {

        return super.createResults() + includesTool.createResults() + TaskResults(this, OptionsMetaDataTask())
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        includesTool.toolPane = SharedToolPane(this)
    }

    override fun detaching() {
        super<ListTableTool>.detaching()
        autoRefresh.unwatchAll()
    }


    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)

    override fun run() {
        shortTitle = optionsNameP.value
        list.clear()

        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, directoryP.value!!)
        for (option in optionsFile.listOptions()) {
            list.add(option)
        }

        includesTool.optionsNameP.value = optionsNameP.value
        includesTool.directoryP.value = directoryP.value
        includesTool.run()

        autoRefresh.unwatchAll()
        autoRefresh.watch(optionsFile.file)

        list.sort()
    }

    fun taskEdit(option: Option): EditOptionTask {
        return EditOptionTask(getFileOptions(), option)
    }

    fun taskCopy(option: Option): CopyOptionTask {
        return CopyOptionTask(getFileOptions(), option)
    }

    fun taskNew(): NewOptionTask {
        return NewOptionTask(getFileOptions())
    }

    fun taskDelete(option: Option): DeleteOptionTask {
        return DeleteOptionTask(getFileOptions(), option)
    }


    open class EditOptionTask(
            val fileOptions: FileOptions,
            val option: Option,
            name: String = "editOption")

        : AbstractTask() {

        final override val taskD = TaskDescription(name)

        val code = StringParameter("code", value = option.code)

        val aliases = MultipleParameter("aliases", value = option.aliases) { StringParameter("") }

        val label = StringParameter("label", value = option.label)

        var flagsGroupP = GroupParameter("flags")

        //var rowGroupP = GroupParameter("rowGroup", label = "")

        var isRow = BooleanParameter("isRow", value = option.isRow)

        var isMultiple = BooleanParameter("isMultiple", value = option.isMultiple)

        val paddingP = InformationParameter("padding", label = "", information = "")

        //var otherGroupP = GroupParameter("otherGroup", label = "")

        var refresh = BooleanParameter("refresh", value = option.refresh)

        var newTab = BooleanParameter("newTab", value = option.newTab)

        var prompt = BooleanParameter("prompt", value = option.prompt)

        val shortcutP = ShortcutParameter("shortcut", value = option.shortcut)

        var groovyScriptP = ScriptParameter("groovyScript",
                rows = 10, columns = 40, value = if (option is GroovyOption) option.script else "",
                scriptVariables = fileOptions.scriptVariables)

        val scriptOrTaskP = OneOfParameter("action", message = "Action Type")

        var taskP = TaskParameter("task", value = if (option is TaskOption) option.task else null, taskFactory = RegisteredTaskFactory())

        init {
            taskP.scriptVariables = fileOptions.scriptVariables

            if (option is GroovyOption) {

                groovyScriptP.value = option.script
                scriptOrTaskP.value = groovyScriptP

            } else if (option is TaskOption) {

                taskP.value = option.task.copy()
                scriptOrTaskP.value = taskP
            }

            flagsGroupP.addParameters(isRow, isMultiple, paddingP, refresh, newTab, prompt)
            flagsGroupP.gridLayout(labelsAbove = false, columns = 3, isBoxed = true)

            taskD.addParameters(code, aliases, label, flagsGroupP, scriptOrTaskP, shortcutP)
            scriptOrTaskP.addParameters(taskP, groovyScriptP)

            isRow.listen {
                isMultiple.hidden = isRow.value == false
            }
            isMultiple.hidden = isRow.value == false
        }

        override fun run() {
            val option = update()
            save(option)
        }

        open fun update(): Option {

            val newOption: Option
            if (scriptOrTaskP.value == groovyScriptP) {
                newOption = GroovyOption(groovyScriptP.value)

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
            newOption.shortcut = shortcutP.keyCodeCombination

            return newOption
        }

        open fun save(newOption: Option) {
            fileOptions.removeOption(option)
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class NewOptionTask(fileOptions: FileOptions)
        : EditOptionTask(fileOptions, TaskOption(NullTask()), name = "newOption") {

        override fun save(newOption: Option) {
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class CopyOptionTask(fileOptions: FileOptions, option: Option)
        : EditOptionTask(fileOptions, option.copy(), name = "copyOption") {

        override fun save(newOption: Option) {
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

    inner class OptionsMetaDataTask() : AbstractTask() {

        override val taskD = TaskDescription("optionsMetaData")

        val scriptVariables = ScriptVariables()

        val fileOptions = getFileOptions()

        val commentsP = StringParameter("comments", required = false, rows = 6,
                value = fileOptions.comments)

        val rowFilterP = ScriptParameter("rowFilterScript", required = false, rows = 5,
                value = fileOptions.rowFilterScript?.source ?: "", scriptVariables = scriptVariables)

        val infoP = InformationParameter("info", information =
        """The following parameters are optional, and if filled in correctly, will allow the "…" button in script fields to show information about the row and tool classes.""")

        val rowClassNameP = StringParameter("rowClassName", required = false, value = fileOptions.rowClassName)

        val toolClassNameP = StringParameter("toolClassName", required = false, value = fileOptions.toolClassName)

        init {
            taskD.addParameters(commentsP, rowFilterP, infoP, rowClassNameP, toolClassNameP)

            scriptVariables.add("row", rowClassNameP.value)
            scriptVariables.add("tool", toolClassNameP.value)

            rowClassNameP.listen {
                scriptVariables.add("row", rowClassNameP.value)
            }
            toolClassNameP.listen {
                scriptVariables.add("tool", toolClassNameP.value)
            }
        }


        override fun run() {
            fileOptions.comments = commentsP.value
            fileOptions.rowFilterScript = if (rowFilterP.value == "") null else GroovyScript(rowFilterP.value)
            fileOptions.rowClassName = rowClassNameP.value
            fileOptions.toolClassName = toolClassNameP.value
            fileOptions.save()
        }
    }
}
