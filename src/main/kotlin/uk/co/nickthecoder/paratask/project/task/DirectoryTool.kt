package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.table.ModifiedColumn
import uk.co.nickthecoder.paratask.project.table.SizeColumn
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class DirectoryTool() : AbstractTool() {

    override val taskD = TaskDescription("directory", description = "List a single Directory")

    val directory = FileParameter("directory", expectFile = false, mustExist = true, value = homeDirectory)

    constructor(directory: File) : this() {
        this.directory.value = directory
    }

    init {
        taskD.addParameters(directory)
    }

    var results = mutableListOf<WrappedFile>()

    override fun run() {
        results.clear()
        val lister = FileLister(onlyFiles = null)
        results.addAll(lister.listFiles(directory.value!!).map { WrappedFile(it) })
    }

    override fun createResults(): List<Results> = singleResults(DirectoryResults())

    class WrappedFile(val file: File) {
        val icon by lazy {
            ParaTaskApp.imageResource("filetypes/${if (file.isDirectory()) "directory" else "file"}.png")
        }
    }

    inner class DirectoryResults() : AbstractTableResults<WrappedFile>(this@DirectoryTool, results) {

        init {
            columns.add(Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
            columns.add(Column<WrappedFile, String>("name") { it.file.name })
            columns.add(Column<WrappedFile, String>("path") { it.file.path })
            columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
            columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTool(DirectoryTool()).go(args)
}
