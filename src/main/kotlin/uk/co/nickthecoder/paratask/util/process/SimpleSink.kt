package uk.co.nickthecoder.paratask.util.process

open class SimpleSink(val bufferSize: Int = 200) : Sink {

    var reader: java.io.Reader? = null

    override fun setStream(stream: java.io.InputStream) {
        reader = java.io.InputStreamReader(stream)
    }

    override fun run() {

        val reader = this.reader

        if (reader == null) {
            return
        }

        try {
            val buffer = CharArray(bufferSize)

            while ( true ) {
                var amount: Int = reader.read(buffer, 0, bufferSize)
                if ( amount == -1 ) {
                    break
                }
                sink(buffer, amount);
            }
        } catch (e: java.io.IOException) {
            sinkError(e);
        } finally {
            reader.close();
        }
    }

    protected open fun sink(buffer: CharArray, len: Int) {
        // Does nothing - throws away the output
    }

    protected fun sinkError(e: java.io.IOException) {
        e.printStackTrace();
    }

}