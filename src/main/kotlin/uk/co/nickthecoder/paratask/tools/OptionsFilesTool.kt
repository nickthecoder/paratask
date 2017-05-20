package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.options.FileOptions
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.File

class OptionsFilesTool : AbstractTableTool<FileOptions>() {

    override val taskD = TaskDescription("optionsFiles", description = "Work with all Option Files")

    val directory = Preferences.createOptionsDirectoryParameter()

    init {
        taskD.addParameters(directory)
    }

    override fun createColumns() {
        columns.add(Column<FileOptions, String>("name") { fileOptions -> fileOptions.file.nameWithoutExtension() })
        columns.add(Column<FileOptions, String>("path") { fileOptions -> fileOptions.file.path })
    }

    override fun run() {
        list.clear()
        val chosenDirectory = directory.value
        if (chosenDirectory == null) {
            for (dir in Preferences.optionsPath) {
                add(dir)
            }
        } else {
            add(chosenDirectory)
        }
    }

    private fun add(directory: File) {
        val fileLister = FileLister(extensions = listOf("json"))
        val files = fileLister.listFiles(directory)
        files.map { it.nameWithoutExtension() }
            .forEach { list.add(OptionsManager.getFileOptions(it, directory)) }
    }
}

fun main(args: Array<String>) {
    ToolParser(OptionsFilesTool()).go(args)
}
