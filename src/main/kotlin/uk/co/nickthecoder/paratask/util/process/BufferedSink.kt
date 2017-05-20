package uk.co.nickthecoder.paratask.util.process

open class BufferedSink() : Sink {

    var reader: java.io.BufferedReader? = null

    var handler: ((String) -> Unit)? = null

    constructor(handler: (String) -> Unit) : this() {
        this.handler = handler
    }

    override fun setStream(stream: java.io.InputStream) {
        reader = java.io.BufferedReader(java.io.InputStreamReader(stream))
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
        } catch (e: java.io.IOException) {
            sinkError(e);
        } finally {
            reader.close();
        }
    }

    protected open fun sink(line: String) {
        handler?.let { it(line) }
    }

    protected open fun sinkError(e: java.io.IOException) {
        // Do nothing
    }

}