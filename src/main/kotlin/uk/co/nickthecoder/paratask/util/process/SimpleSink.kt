package uk.co.nickthecoder.paratask.util.process

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

open class SimpleSink(val bufferSize: Int = 200) : Sink {

    var reader: Reader? = null

    override fun setStream(stream: InputStream) {
        reader = InputStreamReader(stream)
    }

    override fun run() {

        val reader = this.reader ?: return

        try {
            val buffer = CharArray(bufferSize)

            while ( true ) {
                val amount: Int = reader.read(buffer, 0, bufferSize)
                if ( amount == -1 ) {
                    break
                }
                sink(buffer, amount)
            }
        } catch (e: IOException) {
            sinkError(e)
        } finally {
            reader.close()
        }
    }

    protected open fun sink(buffer: CharArray, len: Int) {
        // Does nothing - throws away the output
    }

    protected fun sinkError(e: IOException) {
        e.printStackTrace()
    }

}