package uk.co.nickthecoder.paratask.misc

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * Copies and moves files, showing a progress dialog when the operation takes a long time.
 * Holds a queue of operations, and performs the operations one at a time.
 * While sometimes it is more efficient to copy multiple files simultaneously, usually we are IO bound, and
 * even when we aren't from a user perspective, it is better for one operation to complete quickly, rather than
 * overrall efficiency (because they can start working with the files that HAVE been copied).
 * Perhaps, this is just my personal preference, but I hate GUIs that copy multiple files simultaneously.
 */
class FileOperations {

    private var thread: Thread? = null

    private val notQueued = ConcurrentLinkedQueue<FileOperation>() // Currently active, paused, and failed operations.

    private val queue = ConcurrentLinkedQueue<FileOperation>()

    private var dialog: FileOperationsDialog? = null

    companion object {
        /**
         * A shared instance, so that all operations are perfomed from a single queue.
         * However, you may create other instances if you want multiple queues, each with their own dialog box.
         */
        val instance = FileOperations()
    }

    /**
     * Useful in combination with DropFiles.
     */
    fun fileOperation(files: List<File>, dest: File, transferMode: TransferMode) {

        when (transferMode) {
            TransferMode.COPY -> {
                copyFiles(files, dest)
            }
            TransferMode.MOVE -> {
                moveFiles(files, dest)
            }
            TransferMode.LINK -> {
                linkFiles(files, dest)
            }
        }
    }

    fun copyFiles(sources: List<File>, destinationDirectory: File) {
        val files = sources.filter { it.isFile }
        val direcories = sources.filter { it.isDirectory }

        if (files.isNotEmpty()) {
            add(CopyFilesOperation(files, destinationDirectory))
        }
        if (direcories.isNotEmpty()) {
            add(CopyDirectoriesOperation(direcories, destinationDirectory))
        }
    }

    fun moveFiles(sources: List<File>, destinationDirectory: File) {
        val files = sources.filter { it.isFile }
        val directories = sources.filter { it.isDirectory }

        if (files.isNotEmpty()) {
            add(MoveFilesOperation(files, destinationDirectory))
        }
        if (directories.isNotEmpty()) {
            add(MoveDirectoriesOperation(directories, destinationDirectory))
        }
    }

    fun linkFiles(sources: List<File>, destinationDirectory: File) {

        add(LinkFilesOperation(sources, destinationDirectory))
    }

    private fun add(operation: FileOperation) {
        queue.add(operation)
        if (thread == null) {
            thread = FileOperationThread()
            thread?.start()
        }
        delayShowDialog()
    }

    inner class FileOperationThread : Thread("FileOperation") {

        override fun run() {
            var op = queue.poll()
            while (op != null) {
                notQueued.add(op)
                if (op.activate()) {
                    notQueued.remove(op)
                }
                op = queue.poll()
            }
            thread = null
            maybeHideDialog()
        }
    }

    /**
     * Shows the dialog, listing all file operations in progress, queued, and failed
     * If the dialog doesn't already exist, then it will be show after a short period, so that quick operations
     * do not pop up the dialog.
     */
    private fun delayShowDialog() {
        dialog?.let {
            Platform.runLater {
                // Already open
                dialog?.buildContent()
                dialog?.stage?.show()
            }
            return
        }

        // TODO Create a thread, sleep for a while, and then show the dialog
        Platform.runLater {
            dialog = FileOperationsDialog()
            dialog?.placeOnStage()
        }
    }

    /**
     * If there are no more queued, in progress or failed operations, then hide the dialog
     */
    private fun maybeHideDialog() {
        dialog?.let {
            if (queue.size + notQueued.size == 0) {
                dialog = null
                Platform.runLater {
                    it.stage.hide()
                }
            }
        }
    }

    inner class FileOperationsDialog {

        lateinit var stage: Stage

        val whole = ScrollPane()

        val list = VBox()

        fun placeOnStage() {
            // This may be the first JavaFX window, therefore we have to jump through hoops to initialise JavaFX
            // in its annoying way. Grr.
            // (We may have run a task from the COMMAND LINE without prompting the task).
            ParaTaskApp.runFunction { build() }
        }

        private fun build() {
            whole.isFitToWidth = true

            list.styleClass.add("file-operations")
            whole.content = list

            buildContent()

            stage = Stage()
            stage.title = "File Operations"
            stage.scene = Scene(whole, 600.0, 200.0)

            ParaTask.style(stage.scene)

            stage.sizeToScene()
            AutoExit.show(stage)
        }

        internal fun buildContent() {
            list.children.clear()

            notQueued.forEach { addOp(it) }
            queue.forEach { addOp(it) }
        }

        private fun addOp(op: FileOperation) {
            list.children.add(op.build())
        }
    }
}

interface FileOperation : Runnable {

    val operationLabel: String

    fun build(): Node

    fun activate(): Boolean // Return false on failure
}

abstract class AbstractOperation(val sources: List<File>, val destinationDirectory: File) : FileOperation {

    protected var started = false

    protected var stopping = false

    protected var exec: Exec? = null

    protected val errors = StringBuilder()

    protected val borderPane = BorderPane()

    private val messageLabel = Label("")

    override fun build(): Node {
        borderPane.styleClass.add("file-operation")
        val box = HBox()

        box.children.addAll(
                Label(operationLabel + " "),
                fromNode(),
                Label(" to "),
                toNode())
        borderPane.center = box
        return borderPane
    }

    protected fun message(message: String?) {
        Platform.runLater {
            if (message == null) {
                borderPane.bottom = null
            } else {
                messageLabel.text = message
                borderPane.bottom = messageLabel
            }
        }
    }

    override fun activate(): Boolean {
        Platform.runLater {
            borderPane.styleClass.add("active")
        }
        started = true
        try {
            run()

        } catch(e: Exception) {
            Platform.runLater {
                borderPane.styleClass.add("error")
            }
            return false
        } finally {
            Platform.runLater {
                borderPane.styleClass.remove("active")
            }
        }
        return true
    }

    open fun cancel() {
        if (!stopping) {
            stopping = true
            exec?.kill()
        }
    }

    fun storeErrors(exec: Exec) {
        exec.errSink = object : BufferedSink() {
            override fun sink(line: String) {
                errors.appendln(line)
            }
        }
    }

    fun fromNode(): Node {
        val label: Label
        if (sources.isEmpty()) {
            label = Label("Nothing")
        } else if (sources.size == 1) {
            label = Label(sources[0].name)
            label.tooltip = Tooltip(sources[0].path)
        } else {
            val fileCount = sources.filter { !it.isDirectory }.count()
            val directoryCount = sources.filter { it.isDirectory }.count()
            if (fileCount == 0) {
                label = Label("$directoryCount directories")
            } else if (directoryCount == 0) {
                label = Label("$fileCount files")
            } else {
                label = Label("${sources.size} objects")
                //label = Label("$fileCount files, $directoryCount directories")
            }
            if (sources.size > 10) {
                val subset = sources.subList(0, 9)
                val list = subset.joinToString(separator = "\n")
                label.tooltip = Tooltip(list + "\n…")
            } else {
                label.tooltip = Tooltip(sources.joinToString(separator = "\n"))
            }
        }
        clickToShowTooltip(label)
        return label
    }

    fun toNode(): Node {
        val label = Label(destinationDirectory.name)
        label.tooltip = Tooltip(destinationDirectory.path)
        clickToShowTooltip(label)
        return label
    }

    private fun clickToShowTooltip(label: Label) {
        label.styleClass.add("files")
        label.addEventHandler(MouseEvent.MOUSE_CLICKED) { showTooltip(label) }
    }

    fun showTooltip(control: Control) {
        val p = control.localToScene(0.0, control.height)
        val scene = control.scene
        val window = scene.window
        control.tooltip.isAutoHide = true
        control.tooltip.show(window,
                p.x + scene.x + window.x,
                p.y + scene.y + window.y)
    }
}

abstract class AbstractListOperation(sources: List<File>, destinationDirectory: File)
    : AbstractOperation(sources, destinationDirectory) {

    var index = 0

    override fun run() {
        for (index in 0..sources.size - 1) {
            if (stopping) {
                break
            }
            exec = createExec(index)
            storeErrors(exec!!)
            exec?.start()
            val exitStatus = exec?.waitFor()
            if (exitStatus != 0) {
                // TODO Report errors
            }
        }
    }

    abstract fun createExec(i: Int): Exec

}

/**
 * Copies a list of files (not directories - does not recurse)
 */
class CopyFilesOperation(sources: List<File>, destinationDirectory: File)
    : AbstractListOperation(sources = sources, destinationDirectory = destinationDirectory) {

    override val operationLabel = "Copy"

    override fun createExec(i: Int): Exec {
        message("${i + 1} of ${sources.size} : ${sources[i].name}")
        return Exec("cp", "-f", "--", sources[i], File(destinationDirectory, sources[i].name))
    }
}


/**
 * Moves a list of files (not directories - does not recurse)
 */
class MoveFilesOperation(sources: List<File>, destinationDirectory: File)
    : AbstractListOperation(sources, destinationDirectory) {

    override val operationLabel = "Move"

    override fun createExec(i: Int): Exec {
        message("${i + 1} of ${sources.size} : ${sources[i].name}")
        return Exec("mv", "--", sources[i], File(destinationDirectory, sources[i].name))
    }
}

/**
 * Links a list of files or directories - does not recurse
 */
class LinkFilesOperation(sources: List<File>, destinationDirectory: File)
    : AbstractListOperation(sources, destinationDirectory) {

    override val operationLabel = "Link"

    override fun createExec(i: Int): Exec {
        message("${i + 1} of ${sources.size} : ${sources[i].name}")
        return Exec("ln", "-s", "--", sources[i], File(destinationDirectory, sources[i].name))
    }
}

/**
 * Copy/Move files recursively.
 * I attempt to listen to the results of the mv/cp command to give feedback on the
 * number of files processed, and to show the current file being processed.
 * However, when I do this across nfs using my (slow) wifi, stdout isn't read until the end (so no feedback is given).
 * When copying large files locally, this doesn't happen
 * I've tried ionice and renice on the mv/cp command, and also removed the "outSink" stuff, and the output
 * from stdout STILL doesn't appear till the process finishes. Many hours later, and I've given up.
 */
abstract class RecusriveFileOperation(sources: List<File>, destinationDirectory: File)
    : AbstractOperation(sources, destinationDirectory) {

    override fun run() {
        val lister = FileLister(depth = 10, onlyFiles = null, includeHidden = true, includeBase = true)
        var expectedCount = 0
        sources.forEach {
            expectedCount += lister.listFiles(it).size
        }
        message("$expectedCount files/directories")
        exec = createExec()

        var copyCount = 1

        exec?.outSink = BufferedSink { line ->

            if (!line.startsWith("removed")) {
                val middle = line.indexOf("’ -> ‘")
                if (middle > 0) {
                    message("$copyCount of ${expectedCount} : ${line.substring(middle + 6, line.length - 1)}")
                } else {
                    message("$copyCount of ${expectedCount} : $line")
                }
                copyCount++
            }
        }

        exec?.start()
        //val foo = exec?.unixIoniceIdle()
        //val bar = exec?.unixRenice(10)
        exec?.waitFor()

    }

    abstract fun createExec(): Exec
}

/**
 * Copies a list of directories (recursively)
 */
class CopyDirectoriesOperation(sources: List<File>, destinationDirectory: File)
    : RecusriveFileOperation(sources, destinationDirectory) {

    override val operationLabel = "Copy"

    override fun createExec() = Exec("cp", "-rfv", "--", sources, destinationDirectory)
    //override fun createExec() = Exec("/home/nick/bin/test.sh")
}

/**
 * Moves a list of directories (recursively)
 */
class MoveDirectoriesOperation(sources: List<File>, destinationDirectory: File)
    : RecusriveFileOperation(sources, destinationDirectory) {

    override val operationLabel = "Move"

    override fun createExec() = Exec("mv", "-v", "--", sources, destinationDirectory)

}
