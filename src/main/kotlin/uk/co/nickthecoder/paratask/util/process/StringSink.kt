package uk.co.nickthecoder.paratask.util.process

class StringSink : BufferedSink() {

    private val buffer = StringBuilder()

    override fun sink(line: String) {
        if (!buffer.isEmpty()) {
            buffer.append("\n")
        }
        buffer.append(line)
    }

    override fun toString(): String = buffer.toString()
}
