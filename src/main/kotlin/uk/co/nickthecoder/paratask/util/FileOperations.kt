package uk.co.nickthecoder.paratask.util

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
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

    private val notQueued = ConcurrentLinkedQueue<FileOperation>() // Currently active, and failed operations.

    private val queue = ConcurrentLinkedQueue<FileOperation>()

    private var dialog: FileOperationsDialog? = null

    companion object {
        /**
         * A shared instance, so that all operations are perfomed from a single queue.
         * However, you may create other instances, if you want a separate queue.
         */
        val instance = FileOperations()
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
            thread = object : Thread() {
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
            thread?.name = "FileOperations"
            thread?.start()
        }
        delayShowDialog()
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
                Platform.runLater {
                    it.stage.hide()
                    dialog = null
                }
            }
        }
    }

    inner class FileOperationsDialog {

        lateinit var stage: Stage

        lateinit var whole: BorderPane

        lateinit var list: VBox

        fun placeOnStage() {
            // This may be the first JavaFX window, therefore we have to jump through hoops to initialise JavaFX
            // in its annoying way. Grr.
            ParaTaskApp.runFunction { build() }
        }

        private fun build() {
            whole = BorderPane()
            list = VBox()
            whole.center = list

            stage = Stage()
            stage.title = "File Copy/Move Operations"
            stage.scene = Scene(whole)

            ParaTaskApp.style(stage.scene)

            buildContent()

            stage.sizeToScene()
            AutoExit.show(stage)
        }

        internal fun buildContent() {
            list.children.clear()

            notQueued.forEach { addOp(it) }
            queue.forEach { addOp(it) }
        }

        private fun addOp(op: FileOperation) {
            val node = op.createNode()
            list.children.add(node)
        }
    }
}

interface FileOperation : Runnable {
    // TODO Add progress method, cancel method

    fun message(): String

    fun tooltip(): String?

    fun activate(): Boolean // Return false on failure

    fun createNode(): Node
}

abstract class AbstractOperation : FileOperation {

    var started = false

    var stopping = false

    var exec: Exec? = null

    val errors = StringBuilder()

    var label: Label? = null

    override fun createNode(): Node {
        label = Label(message())
        return label!!
    }

    override fun activate(): Boolean {
        started = true
        try {
            run()

        } catch(e: Exception) {
            // TODO Handle exception
            return false
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
}

abstract class AbstractListOperation(val sources: List<File>) : AbstractOperation() {

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
class CopyFilesOperation(sources: List<File>, val destinationDirectory: File) : AbstractListOperation(sources = sources) {

    override fun createExec(i: Int): Exec {
        return Exec("cp", "-f", "--", sources[i], File(destinationDirectory, sources[i].name))
    }

    override fun message(): String =
            if (started) {
                if (sources.size == 1) {
                    "Copying ${sources[0].name} to ${destinationDirectory.name}"
                } else {
                    "Copying file $index of ${sources.size} to ${destinationDirectory.name}"
                }
            } else {
                "Queued : Copy ${sources.size} files to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = $destinationDirectory"
}


/**
 * Moves a list of files (not directories - does not recurse)
 */
class MoveFilesOperation(sources: List<File>, val destinationDirectory: File) : AbstractListOperation(sources = sources) {

    override fun createExec(i: Int): Exec {
        return Exec("mv", "--", sources[i], File(destinationDirectory, sources[i].name))
    }

    override fun message(): String =
            if (started) {
                if (sources.size == 1) {
                    "Moving ${sources[0].name} to ${destinationDirectory.name}"
                } else {
                    "Moving file $index of ${sources.size} to ${destinationDirectory.name}"
                }
            } else {
                "Queued : Move ${sources.size} files to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = $destinationDirectory"
}

/**
 * Links a list of files or directories - does not recurse
 */
class LinkFilesOperation(sources: List<File>, val destinationDirectory: File) : AbstractListOperation(sources = sources) {

    override fun createExec(i: Int): Exec {
        return Exec("ln", "-s", "--", sources[i], File(destinationDirectory, sources[i].name))
    }

    override fun message(): String =
            if (started) {
                if (sources.size == 1) {
                    "Linking ${sources[0].name} to ${destinationDirectory.name}"
                } else {
                    "Linking file $index of ${sources.size} to ${destinationDirectory.name}"
                }
            } else {
                "Queued : Link ${sources.size} files to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = $destinationDirectory"
}

abstract class RecusriveFileOperation(val destinationDirectory: File) : AbstractOperation() {

    var latestFile: String? = null

    var copyCount = 0

    var expectedCount = 1

    override fun run() {
        val lister = FileLister(depth = 10, onlyFiles = false)
        expectedCount = lister.listFiles(destinationDirectory).size

        label?.let { it.text = message() }

        exec = createExec()
        exec?.outSink = object : BufferedSink() {
            override fun sink(line: String) {
                copyCount++
            }
        }
        exec?.start()
        exec?.waitFor()

    }

    abstract fun createExec(): Exec
}

/**
 * Copies a list of directories (recursively)
 */
class CopyDirectoriesOperation(val sources: List<File>, destinationDirectory: File)
    : RecusriveFileOperation(destinationDirectory = destinationDirectory) {

    override fun createExec() = Exec("cp", "-rfv", "--", sources, destinationDirectory)

    override fun message(): String =
            if (started) {
                if (copyCount == 0) {
                    "Preparing"
                } else {
                    "Copying $latestFile (#$copyCount of about $expectedCount)"
                }
            } else {
                "Queued : Copy ${sources.size} directories to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = $destinationDirectory"
}

/**
 * Moves a list of directories (recursively)
 */
class MoveDirectoriesOperation(val sources: List<File>, destinationDirectory: File)
    : RecusriveFileOperation(destinationDirectory = destinationDirectory) {


    override fun createExec() = Exec("mv", "-v", "--", sources, destinationDirectory)

    override fun message(): String =
            if (started) {
                if (copyCount == 0) {
                    "Preparing"
                } else {
                    "Copying $latestFile (#$copyCount of about $expectedCount)"
                }
            } else {
                "Queued : Move ${sources.size} directories to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = $destinationDirectory"

}


