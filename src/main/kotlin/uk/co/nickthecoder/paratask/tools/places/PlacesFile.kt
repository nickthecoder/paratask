/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.util.Resource
import java.io.File
import java.net.URL

class PlacesFile(val file: File) {

    val places = file.readLines().map { parseLine(it) }.toMutableList()

    fun save() {
        file.writeText(places.joinToString(separator = "\n", postfix = "\n"))
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
            return Place(this, Resource(file), label)
        } else {
            return Place(this, Resource(urlString), label)
        }
    }

    fun taskNew(): NewPlaceTask = NewPlaceTask(this)

}

