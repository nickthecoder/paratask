package uk.co.nickthecoder.paratask.util

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

    private val queue = ConcurrentLinkedQueue<FileOperation>()

    companion object {
        /**
         * A shared instance, so that all operations are perfomed from a single queue.
         * However, you may create other instances, if you want a separate queue.
         */
        val instance = FileOperations()
    }

    fun copyFile(source: File, destinationDirectory: File) {
        copyFiles(listOf(source), destinationDirectory)
    }

    fun copyFiles(sources: List<File>, destinationDirectory: File) {
        val files = sources.filter { it.isFile }
        val direcories = sources.filter { it.isDirectory }

        if (files.size > 0) {
            add(CopyFilesOperation(files, destinationDirectory))
        }
        if (direcories.size > 0) {
            add(CopyDirectoriesOperation(direcories, destinationDirectory))
        }
    }

    private fun add(operation: FileOperation) {
        queue.add(operation)
        if (thread == null) {
            thread = object : Thread() {
                override fun run() {
                    var op = queue.poll()
                    while (op != null) {
                        op.run()
                        op = queue.poll()
                    }
                    thread = null
                }
            }
            thread?.name = "FileOperations"
            thread?.start()
        }
    }


    //fun moveFile(source: File, destinationDirectory: File) {
    //    queue.add(MoveOperation(listOf(source), destinationDirectory))
    //}
}

interface FileOperation : Runnable {
    // TODO Add progress method, cancel method

    fun message(): String

    fun tooltip(): String?
}

abstract class AbstractOperation : FileOperation {

    var started = false

    var stopping = false

    var exec: Exec? = null

    val errors = StringBuilder()

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
        started = true
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
        val destination = File(destinationDirectory, sources[index].name)
        return Exec("cp", "--", sources[i], destination)
    }

    override fun message(): String =
            if (started) {
                if (sources.size == 1) {
                    "Copying ${sources[0].name} to ${destinationDirectory.name}"
                } else {
                    "Copying file ${index} of ${sources.size} to ${destinationDirectory.name}"
                }
            } else {
                "Queued : Copy ${sources.size} files to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = ${destinationDirectory}"
}

/**
 * Copies a list of files (not directories - does not recurse)
 */
class CopyDirectoriesOperation(val sources: List<File>, val destinationDirectory: File) : AbstractOperation() {

    var copyCount = 0

    var latestFile: String? = null

    override fun run() {
        started = true
        exec = Exec("cp", "-r", "-v", "--", sources, destinationDirectory)
        exec?.outSink = object : BufferedSink() {
            override fun sink(line: String) {
                copyCount++
            }
        }
        exec?.start()
        exec?.waitFor()
    }

    override fun message(): String =
            if (started) {
                if (copyCount == 0) {
                    "Preparing"
                } else {
                    "Copying ${latestFile}"
                }
            } else {
                "Queued : Copy ${sources.size} directories to ${destinationDirectory.name}"
            }

    override fun tooltip(): String = "Destination = ${destinationDirectory}"
}

//class MoveOperation(sources: List<File>, val destinationDirectory: File) : AbstractListOperation(sources = sources) {

//    override fun createExec(i: Int): Exec {
//        val destination = File(destinationDirectory, sources[index].name)
//        return Exec("mv", "--", sources[i], destination)
//    }

//    override fun message(): String = "Moving file ${index} of ${sources.size} to ${destinationDirectory.name}"

//    override fun tooltip(): String = "Destination = ${destinationDirectory}"
//}
