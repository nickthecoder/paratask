package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.option.FileOptions
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.File

class OptionsFilesTool() : AbstractTableTool<FileOptions>() {

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
        val fileLister = FileLister(extensions = listOf<String>("json"))
        val files = fileLister.listFiles(directory)
        for (file in files) {
            val name = file.nameWithoutExtension()
            list.add(OptionsManager.getFileOptions(name, directory))
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTool(OptionsFilesTool()).go(args)
}
