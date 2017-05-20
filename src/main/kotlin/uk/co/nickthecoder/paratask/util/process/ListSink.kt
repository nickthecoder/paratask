package uk.co.nickthecoder.paratask.util.process

class ListSink : BufferedSink() {

    val list = mutableListOf<String>()

    override fun sink(line: String) {
        list.add(line)
    }
}