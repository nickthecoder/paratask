package uk.co.nickthecoder.paratask.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

open class BufferedSink() : Sink {

    var reader: BufferedReader? = null

    override fun setStream(stream: InputStream) {
        reader = BufferedReader(InputStreamReader(stream))
    }

    override fun run() {

        val reader = this.reader

        if (reader == null) {
            return
        }

        try {
            while (true) {
                val line = reader.readLine()
                if (line == null) {
                    break
                }
                sink(line);
            }
        } catch (e: IOException) {
            sinkError(e);
        } finally {
            reader.close();
        }
    }

    protected open fun sink(line: String) {
        // Does nothing - throws away the output
    }

    protected fun sinkError(e: IOException) {
        // Do nothing
    }

}