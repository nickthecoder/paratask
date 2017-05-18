package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.BaseFileColumn
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.table.FileNameColumn
import uk.co.nickthecoder.paratask.project.table.ModifiedColumn
import uk.co.nickthecoder.paratask.project.table.SizeColumn
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.WrappedFile
import uk.co.nickthecoder.paratask.util.homeDirectory

abstract class AbstractDirectoryTool(name: String, description: String)

    : AbstractTableTool<WrappedFile>() {


    override val taskD = TaskDescription(name = name, description = description)

    val directoryP = FileParameter("directory", expectFile = false, mustExist = true, value = homeDirectory)

    val depthP = IntParameter("depth", value = 1, range = 1..Int.MAX_VALUE)

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)

    val extensionsP = MultipleParameter("extensions") { StringParameter("") }

    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val enterHiddenP = BooleanParameter("enterHidden", value = false)

    val includeBaseP = BooleanParameter("includeBase", value = false)

    val directory
        get() = directoryP.value!!


    init {
        taskD.addParameters(directoryP, depthP, onlyFilesP, extensionsP, includeHiddenP, enterHiddenP, includeBaseP)
    }

    override fun createColumns() {
        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
        if (isTree()) {
            columns.add(BaseFileColumn<WrappedFile>("path", base = directoryP.value!!) { it.file })
        } else {
            columns.add(FileNameColumn<WrappedFile>("name") { it.file })
        }
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })
    }

    override fun run() {
        val lister = FileLister(
                depth = depthP.value!!,
                onlyFiles = onlyFilesP.value,
                includeHidden = includeHiddenP.value!!,
                enterHidden = enterHiddenP.value!!,
                includeBase = includeBaseP.value!!,
                extensions = extensionsP.value
        )

        list.clear()
        list.addAll(lister.listFiles(directoryP.value!!).map { WrappedFile(it) })
    }


    abstract fun isTree(): Boolean

}