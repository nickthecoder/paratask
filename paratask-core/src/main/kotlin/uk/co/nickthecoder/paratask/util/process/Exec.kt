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

package uk.co.nickthecoder.paratask.util.process

import uk.co.nickthecoder.paratask.util.child
import java.io.File
import java.util.concurrent.TimeUnit


val NOT_STARTED = -1000

val INTERRUPTED = -10001

class Exec {

    private val builder: ProcessBuilder

    val osCommand: OSCommand

    constructor(osCommand: OSCommand) {
        this.osCommand = osCommand
        builder = ProcessBuilder(osCommand.command)
        osCommand.directory?.let { builder.directory(it) }
    }

    constructor(command: String, vararg arguments: Any?) {
        this.osCommand = OSCommand(command, *arguments)
        builder = ProcessBuilder(this.osCommand.command)
    }

    var process: Process? = null

    var outSink: Sink? = SimpleSink()

    var errSink: Sink? = SimpleSink()

    var outThread: Thread? = null

    var errThread: Thread? = null

    val listeners = ProcessNotifier()

    fun dir(dir: File?): Exec {
        builder.directory(dir)
        return this
    }

    fun inheritOut(): Exec {
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        outSink = null
        return this
    }

    fun inheritErr(): Exec {
        builder.redirectError(ProcessBuilder.Redirect.INHERIT)
        errSink = null
        return this
    }

    fun mergeErrWithOut(): Exec {
        builder.redirectErrorStream(true)
        errSink = null
        return this
    }

    fun listen(listener: () -> Unit) {
        listeners.add(object : ProcessListener {
            override fun finished(process: Process) {
                listener()
            }
        })
    }

    fun start(): Exec {

        if (process != null) {
            return this // Already started.
        }

        val process = builder.start()
        this.process = process

        listeners.start(process)

        outSink?.let { sink ->
            sink.setStream(process.inputStream)
            outThread = Thread(sink)
            outThread?.isDaemon = true
            outThread?.name = "Exec.OutputSink"
            outThread?.start()
        }
        errSink?.let { sink ->
            sink.setStream(process.errorStream)
            errThread = Thread(sink)
            errThread?.isDaemon = true
            errThread?.name = "Exec.ErrorSink"
            errThread?.start()
        }

        return this
    }

    fun kill(forcibly: Boolean = false) {
        process?.let {
            try {
                it.outputStream.close()
                it.inputStream.close()
                it.errorStream.close()
            } catch (e: Exception) {
            }
            if (forcibly) {
                it.destroyForcibly()
            } else {
                it.destroy()
            }
        }
    }

    fun waitFor(duration: Long = 0, units: TimeUnit = TimeUnit.SECONDS, killOnTimeout: Boolean = false): Int {

        val timeoutThread = Timeout(units.toMillis(duration))
        if (duration > 0) {
            timeoutThread.start()
        }

        try {
            try {
                process?.let {
                    val result = it.waitFor()

                    // Let the threads reading input finish before we end, otherwise the output may be truncated.
                    outThread?.join()
                    errThread?.join()
                    return result
                }

            } catch (e: InterruptedException) {
                if (killOnTimeout) {
                    kill()
                }
                return INTERRUPTED
            }
        } finally {
            timeoutThread.done = true
        }

        return NOT_STARTED
    }


    override fun toString(): String {
        return "Exec : $osCommand"
    }

    /**
     * Attempts to get the process ID. This is not cross platform, as the name suggests.
     */
    fun unixPID(): Long? {
        return process?.unixPID()
    }

    /**
     * Uses /proc/PID/cwd to find the Process's current working directory.
     * Returns null for non-linux environments
     * Also returns null if the process has ended, or an error occurs,
     * such as the user does not own the process.
     */
    fun linuxCurrentDirectory(): File? {
        return process?.linuxCurrentDirectory()
    }

    /**
     * Runs ionice -c 3 -p [PID of this.process].
     * Returns the Exec of the ionice command, or null if the PID could not be found.
     */
    fun unixIoniceIdle() {
        process?.unixIoniceIdle()
    }

    /**
     * Runs the unix renice command for this exec's process
     * Returns the Exec of the renice command, or null if the PID could not be found.
     */
    fun unixRenice(priority: Int = 10) {
        process?.unixRenice(priority)
    }

    class Timeout(val timeoutMillis: Long) : Thread() {
        init {
            this.name = "Exec.Timeout"
            this.isDaemon = true
        }

        var done: Boolean = false

        val calling: Thread = Thread.currentThread()

        override fun run() {
            sleep(timeoutMillis)
            if (!done) {
                calling.interrupt()
            }
        }
    }

}

/**
 * Attempts to get the process ID. This is not cross platform, as the name suggests.
 */
fun Process.unixPID(): Long? {

    // Newer versions of com.pty4j.unix.UnixPtyProcess have a "getPid" method, so let's try that first.
    try {
        val method = this.javaClass.getMethod("getPid")
        return (method.invoke(this) as Int?)?.toLong()
    } catch (e1: Exception) {
        // Do nothing
    }

    // Ok, now let's see if we can get the "pid" attribute, which is private in java.lang.UNIXProcess and com.pty4j.unix.UnixPtyProcess
    // Note in UNIXProcess, the pid is a long, whereas in UnixPtyProcess pid is an int.
    try {
        val field = this.javaClass.getDeclaredField("pid")
        field?.isAccessible = true
        val pid = field?.getLong(this)
        field?.isAccessible = false
        return pid

    } catch (e2: Exception) {
         // Do nothing
    }

    return null
}

/**
 * Uses /proc/PID/cwd to find the Process's current working directory.
 * Returns null for non-linux environments
 * Also returns null if the process has ended, or an error occurs,
 * such as the user does not own the process.
 */
fun Process.linuxCurrentDirectory(): File? {
    if (!System.getProperty("os.name").startsWith("Linux")) {
        return null
    }

    val cwd = File("/proc").child(unixPID().toString(), "cwd")
    if (cwd.exists()) {
        try {
            return cwd.canonicalFile
        } catch (e: Exception) {
        }
    }
    return null
}

/**
 * Runs ionice -c 3 -p [PID of this.process].
 * Returns the Exec of the ionice command, or null if the PID could not be found.
 */
fun Process.unixIoniceIdle(): Exec? {
    val pid = unixPID()
    if (pid != null) {
        val exec = Exec("ionice", "-c", "3", "-p", pid)
        exec.start()
        return exec
    }
    return null
}

/**
 * Runs the unix renice command for this exec's process
 * Returns the Exec of the renice command, or null if the PID could not be found.
 */
fun Process.unixRenice(priority: Int = 10): Exec? {
    val pid = unixPID()
    if (pid != null) {
        val exec = Exec("renice", "-n", priority, "-p", pid)
        exec.start()
        return exec
    }
    return null
}


