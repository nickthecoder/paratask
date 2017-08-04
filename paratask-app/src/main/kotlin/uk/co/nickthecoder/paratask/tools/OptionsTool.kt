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

import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.RegisteredTaskFactory
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.DragHelper
import uk.co.nickthecoder.paratask.table.TableToolDropHelper
import uk.co.nickthecoder.paratask.options.*
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.BooleanColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.misc.AutoRefreshTool

class OptionsTool : AbstractTableTool<Option>, AutoRefreshTool {

    override val taskD = TaskDescription("options", description = "Work with Options")

    val optionsNameP = StringParameter("optionsName")

    val resourceDirectoryP = ResourceParameter("directory", expectFile = false)

    var includesTool: IncludesTool = IncludesTool()

    var dropHelper: TableToolDropHelper<List<Option>, Option> = object :
            TableToolDropHelper<List<Option>, Option>(Option.dataFormat, this, modes = TransferMode.ANY) {

        override fun acceptDropOnNonRow() = arrayOf(TransferMode.COPY, TransferMode.MOVE)

        override fun acceptDropOnRow(row: Option) = null

        override fun droppedFilesOnRow(row: Option, content: List<Option>, transferMode: TransferMode): Boolean {
            return false
        }

        override fun droppedFilesOnNonRow(content: List<Option>, transferMode: TransferMode): Boolean {
            val fileOptions = getFileOptions()
            for (option in content) {
                fileOptions.addOption(option.copy())
            }
            fileOptions.save()
            return true
        }
    }


    init {
        taskD.addParameters(optionsNameP, resourceDirectoryP)
    }

    override val resultsName = "Options"

    val tool: Tool?

    constructor() : super() {
        tool = null
    }

    constructor(tool: Tool) : super() {
        this.tool = tool
        optionsNameP.value = tool.optionsName
        resourceDirectoryP.value = Preferences.optionsPath[0]
    }

    constructor(fileOptions: FileOptions) : this() {
        optionsNameP.value = fileOptions.name
        resourceDirectoryP.value = fileOptions.resource.parentResource()
    }

    constructor(optionsName: String) : this() {
        optionsNameP.value = optionsName
        resourceDirectoryP.value = Preferences.optionsPath[0]
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

    override fun createTableResults(): TableResults<Option> {
        val tableResults = super.createTableResults()

        DragHelper<List<Option>>(Option.dataFormat, tableResults.tableView, done = { event, content -> onDragComplete(event, content) }) {
            tableResults.tableView.selectionModel.selectedItems.map { it.row }
        }

        dropHelper.table = tableResults.tableView

        return tableResults
    }

    fun onDragComplete(event: DragEvent, content: List<Option>) {
        if (event.acceptedTransferMode == TransferMode.MOVE) {
            val fileOptions = getFileOptions()
            content.map { fileOptions.find(it.code) }.filterNotNull().forEach {
                fileOptions.removeOption(it)
            }
            fileOptions.save()
        }
    }

    override fun createResults(): List<Results> {

        return super.createResults() + includesTool.createResults() + TaskResults(this, OptionsMetaDataTask())
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        includesTool.toolPane = SharedToolPane(this)
        dropHelper.attached(toolPane)
    }

    override fun detaching() {
        super<AbstractTableTool>.detaching()
        super<AutoRefreshTool>.detaching()
        dropHelper.detaching()
    }


    fun getFileOptions() = OptionsManager.getFileOptions(optionsNameP.value, resourceDirectoryP.value!!)

    override fun run() {
        shortTitle = optionsNameP.value
        list.clear()
        val optionsFile = OptionsManager.getFileOptions(optionsNameP.value, resourceDirectoryP.value!!)

        for (option in optionsFile.listOptions()) {
            list.add(option)
        }

        includesTool.optionsNameP.value = optionsNameP.value
        includesTool.resourceDirectoryP.value = resourceDirectoryP.value
        includesTool.run()

        optionsFile.resource.file?.let { watch(it) }
    }

    override fun refresh() {
        // Ahh what a bodge ;-)
        // There is a race condition between FileOptions reloading itself, and OptionsTool refreshing.
        // By sleeping for a second, it is likely that FileOptions will win the race, and the results
        // will be as expected. Obviously, by no means fool proof, but good enough for me!
        Thread(Runnable {
            Thread.sleep(1000)
            super.refresh()
        }).start()
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

        final override val taskD = TaskDescription(name)

        val code = StringParameter("code", value = option.code)

        val aliases = MultipleParameter("aliases", value = option.aliases) { StringParameter("") }

        val label = StringParameter("label", value = option.label)

        var isRow = BooleanParameter("isRow", value = option.isRow)

        var isMultiple = BooleanParameter("isMultiple", value = option.isMultiple)

        var refresh = BooleanParameter("refresh", value = option.refresh)

        var newTab = BooleanParameter("newTab", value = option.newTab)

        var prompt = BooleanParameter("prompt", value = option.prompt)

        val scriptOrTaskP = OneOfParameter("typeOfAction")

        var script = StringParameter("script",
                rows = 10, columns = 40, style = "script",
                value = if (option is GroovyOption) option.script else "")

        var taskP = TaskParameter("task", value = if (option is TaskOption) option.task else null, taskFactory = RegisteredTaskFactory())

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

        override fun save(newOption: Option) {
            fileOptions.addOption(newOption)
            fileOptions.save()
        }
    }

    open class CopyOptionTask(fileOptions: FileOptions, option: Option, tool: Tool?)
        : EditOptionTask(fileOptions, option.copy(), name = "copyOption", tool = tool) {

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

        val fileOptions = getFileOptions()

        val commentsP = StringParameter("comments", required = false, rows = 6,
                value = fileOptions.comments)

        val rowFilterP = StringParameter("rowFilterScript", required = false, style = "script", rows = 10,
                value = fileOptions.rowFilterScript?.source ?: "")

        init {
            taskD.addParameters(commentsP, rowFilterP)
        }

        override fun run() {
            fileOptions.comments = commentsP.value
            fileOptions.rowFilterScript = if (rowFilterP.value == "") null else GroovyScript(rowFilterP.value)
            fileOptions.save()
        }
    }
}
