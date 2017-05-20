package uk.co.nickthecoder.paratask.util.process

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

    fun dir(dir: File?):Exec {
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
            throw RuntimeException("Process already started")
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
        val timeoutThread = Exec.Timeout(units.toMillis(duration))

        try {
            try {
                process?.let { return it.waitFor() }
            } catch (e: InterruptedException) {
                if (killOnTimeout) {
                    kill()
                }
                return INTERRUPTED
            }
            if (duration > 0) {
                timeoutThread.start()
            }
        } finally {
            timeoutThread.done = true

            // Let the threads reading input finish before we end, otherwise the output may be truncated.
            outThread?.join()
            errThread?.join()
        }

        return NOT_STARTED
    }

    class Timeout(val timeoutMillis: Long) : Thread() {
        init {
            this.name = "Exec.Timeout"
            this.isDaemon = true
        }

        var done: Boolean = false

        val calling :Thread = Thread.currentThread()

        override fun run() {
            sleep(timeoutMillis)
            if (!done) {
                calling.interrupt()
            }
        }
    }
}

