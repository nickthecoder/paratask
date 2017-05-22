package uk.co.nickthecoder.paratask.tools.places

import java.io.File
import java.net.URL

class PlacesFile(val file: File) {

    val places = file.readLines().map { parseLine(it) }.toMutableList()

    fun save() {
        val writer = file.printWriter()
        writer.use {
            for (place in places) {
                writer.println(place.toString())
            }
        }
    }

    private fun parseLine(line: String): Place {
        val space = line.indexOf(' ')
        val urlString = if (space >= 0) line.substring(0, space) else line
        var label: String
        if (space >= 0) {
            label = line.substring(space).trim()
        } else {
            try {
                val url = URL(urlString)
                val filePart = File(url.file)
                label = filePart.name
            } catch (e: Exception) {
                label = ""
            }
        }

        // TODO Handle files relative to this places file. e.g.  relative:foo/bar%20bar/baz
        val url = URL(urlString)
        if (urlString.startsWith("file:")) {
            val file = File(url.toURI())
            return FilePlace(this, file, label)
        } else {
            return URLPlace(this, urlString, label)
        }
    }

    fun taskNew(): NewPlaceTask = NewPlaceTask(this)

}

