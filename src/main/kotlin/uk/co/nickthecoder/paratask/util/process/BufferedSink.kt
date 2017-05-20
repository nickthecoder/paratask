package uk.co.nickthecoder.paratask.util.process

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

open class BufferedSink() : Sink {

    var reader: BufferedReader? = null

    var handler: ((String) -> Unit)? = null

    constructor(handler: (String) -> Unit) : this() {
        this.handler = handler
    }

    override fun setStream(stream: InputStream) {
        reader = BufferedReader(InputStreamReader(stream))
    }

    override fun run() {

        val reader = this.reader ?: return

        try {
            while (true) {
                val line = reader.readLine() ?: break
                sink(line)
            }
        } catch (e: IOException) {
            sinkError(e)
        } finally {
            reader.close()
        }
    }

    protected open fun sink(line: String) {
        handler?.let { it(line) }
    }

    protected open fun sinkError(e: IOException) {
        // Do nothing
    }

}