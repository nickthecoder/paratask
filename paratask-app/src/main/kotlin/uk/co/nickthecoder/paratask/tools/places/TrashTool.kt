package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractCommandTask
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.FileNameColumn
import uk.co.nickthecoder.paratask.table.LocalDateTimeColumn
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.HasDropHelper
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class TrashTool :
        AbstractCommandTool<TrashTool.TrashFile>(),
        SingleRowFilter<TrashTool.TrashFile>,
        HasDropHelper {

    override val taskD = TaskDescription(name = "trash", description = "List Deleted Files")

    override val rowFilter = RowFilter(this, columns, TrashFile(File(""), LocalDateTime.now()))

    override val dropHelper = DropFiles(arrayOf(TransferMode.MOVE)) { _, files ->
        files.filter { it.exists() }.forEach {
            trashFile(it)
        }
        println("Running? ${toolPane?.parametersPane}")
        toolPane?.parametersPane?.run()
    }

    init {
        taskD.addParameters()

        columns.add(FileNameColumn<TrashFile>("name", getter = { it.file }))
        columns.add(Column<TrashFile, File>("path", getter = { it.file }))
        columns.add(LocalDateTimeColumn<TrashFile>("deleted", getter = { it.deletedOn }))
    }

    override fun createCommand(): OSCommand {
        return OSCommand("trash-list")
    }

    override fun processLine(line: String) {
        val matcher = listLinePattern.matcher(line.trim())
        if (matcher.matches()) {

            val dateTimeString = matcher.group(1)
            val path = matcher.group(2)
            val deletedOn = LocalDateTime.parse(dateTimeString, dateTimePattern)

            list.add(TrashFile(File(path), deletedOn))
        }
    }

    override fun execFinished() {
        list.sortBy { it.file }
    }

    fun createHeaderRows(dirP: FileParameter): Header = Header(this, dirP)

    override fun createResults(): List<Results> {
        val tableResults = TableResults(this, list, "Trash", columns, rowFilter = rowFilter)
        tableResults.dropHelper = dropHelper
        return listOf(tableResults)
    }

    companion object {

        private val listLinePattern = Pattern.compile("^([^ ]* [^ ]*) (.*)$")

        private val restoreLinePattern = Pattern.compile("^([0-9]*) [^ ]* [^ ]* (.*)$")

        private val dateTimePattern = DateTimeFormatter.ofPattern("y-M-d k:m:s")

        fun trashFile(file: File) {
            Exec("trash-put", file).start().waitFor(1)
        }

        fun restoreFile(file: File) {
            // Alas, I couldn't manage to read the output from the restore-trash process without sending it data.
            // Not sure why. Buffering issues maybe.
            // So instead, I run the command twice, once to find the number of the file to restore, and again to
            // restore the file (once I know the number I need to feed it.

            // Fingers crossed that the number is still valid on the second pass!!!

            // Hopefully I'm missing something, and there is a way to restore a file just by giving it the name,
            // but I couldn't find it. Oh well.

            // I've logged an issue here : https://github.com/andreafrancia/trash-cli/issues/105

            var restoreNumber = ""

            val exec = Exec("restore-trash").dir(file.parentFile)
            exec.outSink = BufferedSink { line ->
                val matcher = restoreLinePattern.matcher(line.trim())
                if (matcher.matches()) {
                    if (matcher.group(2) == file.path) {
                        restoreNumber = matcher.group(1)
                    }
                }
            }

            exec.start()
            // Send a new line to end the process without it restoring anything
            exec.process?.outputStream?.write("\n".toByteArray())
            exec.process?.outputStream?.flush()
            exec.process?.outputStream?.close()

            exec.waitFor(5)
            if (exec.process?.isAlive == true) exec.kill()

            if (restoreNumber.isNotBlank()) {
                val exec2 = Exec("restore-trash").dir(file.parentFile)
                exec2.start()
                exec2.process?.outputStream?.write("$restoreNumber\n".toByteArray())
                exec2.process?.outputStream?.flush()
                exec2.process?.outputStream?.close()
                exec2.waitFor(30)
                if (exec2.process?.isAlive == true) exec.kill()
            }

        }
    }

    class TrashFile(file: File, val deletedOn: LocalDateTime) : WrappedFile(file)

    class EmptyTrashTask : AbstractCommandTask() {
        override val taskD = TaskDescription("emptyTrash")

        val daysAgoP = IntParameter("daysAgo", value = 7, required = false)

        init {
            taskD.addParameters(daysAgoP)
        }

        override fun createCommand(): OSCommand {
            return OSCommand("trash-empty", daysAgoP.value)
        }
    }


    class RestoreFilesTask : AbstractTask() {
        override val taskD = TaskDescription("restoreFiles")

        val filesP = MultipleParameter("files") {
            FileParameter("file", mustExist = false, expectFile = null)
        }

        init {
            taskD.addParameters(filesP)
        }

        override fun run() {
            filesP.innerParameters.forEach { fileParameter ->
                fileParameter.value?.let { file ->
                    restoreFile(file)
                }
            }
        }
    }

    class MoveToTrashTask : AbstractTask() {
        override val taskD = TaskDescription("moveToTrash")

        val filesP = MultipleParameter("files") {
            FileParameter("file", mustExist = true, expectFile = null)
        }

        init {
            taskD.addParameters(filesP)
        }

        override fun run() {
            filesP.innerParameters.forEach { fileParameter ->
                fileParameter.value?.let { file ->
                    trashFile(file)
                }
            }
        }
    }
}
