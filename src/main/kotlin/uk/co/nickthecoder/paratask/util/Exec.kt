package uk.co.nickthecoder.paratask.util

import java.util.concurrent.TimeUnit

val NOT_STARTED = -1000

val INTERRUPTED = -10001

class Exec(val command: Command) {

    private val builder = ProcessBuilder(command.command)

    var process: Process? = null

    var outSink: Sink? = SimpleSink()

    var errSink: Sink? = SimpleSink()

    var outThread: Thread? = null

    var errThread: Thread? = null

    val listeners = ProcessNotifier()

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

    fun start(): Exec {

        if (process != null) {
            throw RuntimeException("Process already started")
        }

        val process = builder.start()
        this.process = process

        listeners.start(process)

        outSink?.let {
            it.setStream(process.inputStream)
            outThread = Thread(it).apply { start() }
        }
        errSink?.let {
            it.setStream(process.errorStream)
            errThread = Thread(it).apply { start() }
        }

        return this
    }

    fun kill(forcibly: Boolean = false) {
        process?.let {
            if (forcibly) {
                it.destroyForcibly()
            } else {
                it.destroy()
            }
        }
    }

    fun waitFor(duration: Long = 0, units: TimeUnit = TimeUnit.SECONDS, killOnTimeout: Boolean = false): Int {
        val timeoutThread = Timeout(units.toMillis(duration))

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
            outThread?.let { it.join() }
            errThread?.let { it.join() }
        }

        return NOT_STARTED
    }

    class Timeout(val timeoutMillis: Long) : Thread() {
        var done: Boolean = false

        val calling = Thread.currentThread()

        override fun run() {
            sleep(timeoutMillis);
            if (!done) {
                calling.interrupt()
            }
        }
    }
}

