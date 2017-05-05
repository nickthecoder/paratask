package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.File

class AllOptionsTool() : AbstractTool() {

    private val fileOptionsList = mutableListOf<FileOptions>()

    override val taskD = TaskDescription("allOptions", description = "Lists all Option Files")

    override fun run(values: Values) {
        fileOptionsList.clear()
        for (directory in Preferences.optionsPath) {
            add(directory)
        }
    }

    private fun add(directory: File) {
        val fileLister = FileLister().extension("json")
        val files = fileLister.listFiles(directory)
        for (file in files) {
            val name = file.nameWithoutExtension()
            fileOptionsList.add(OptionsManager.getFileOptions(name, directory))
        }
    }

    override fun updateResults() {
        toolPane?.updateResults(AllOptionsResults(this))
    }

    class AllOptionsResults(tool: AllOptionsTool) : AbstractTableResults<FileOptions>(tool, tool.fileOptionsList) {

        init {
            columns.add(Column<FileOptions, String>("name") { fileOptions -> fileOptions.file.nameWithoutExtension() })
            columns.add(Column<FileOptions, String>("path") { fileOptions -> fileOptions.file.path })
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTool(HomeTool()).go(args)
}
