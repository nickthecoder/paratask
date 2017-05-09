package uk.co.nickthecoder.paratask.util

class ListSink() : BufferedSink() {

    val list = mutableListOf<String>()

    override fun sink(line: String) {
        list.add(line)
    }
}