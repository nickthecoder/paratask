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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.StringParameter

open class EditPlaceTask(protected val place: Place, name: String = "editPlace")
    : AbstractTask() {

    final override val taskD = TaskDescription(name)

    val file = FileParameter("file", expectFile = null, required = false)

    val url = StringParameter("url", required = false)

    val label = StringParameter("label", required = false)

    init {
        taskD.addParameters(file, url, label)

        if (place is FilePlace) {
            file.value = place.file
        } else {
            url.value = place.urlString
        }
        label.value = place.label

        url.listen { parameterChanged(it) }
        file.listen { parameterChanged(it) }
    }

    fun parameterChanged(event: ParameterEvent) {
        if (event.parameter === file) {
            if (file.value != null) {
                url.value = ""
            }
        }
        if (event.parameter === url) {
            if (url.value != "") {
                file.value = null
            }
        }
    }


    private fun createPlace(): Place {
        return if (file.value == null) {
            URLPlace(place.placesFile, url.value, label.value)
        } else {
            FilePlace(place.placesFile, file.value!!, label.value)
        }
    }

    override fun customCheck() {
        if (file.value == null && (url.value == "")) {
            throw ParameterException(url, "You must enter a URL or a File")
        }
        try {
            createPlace()
        } catch(e: Exception) {
            throw ParameterException(url, "Invlid URL")
        }
    }

    override fun run() {
        val index = place.placesFile.places.indexOf(place)
        place.placesFile.places.remove(place)

        val newPlace = createPlace()
        if (index >= 0) {
            place.placesFile.places.add(index, newPlace)
        } else {
            place.placesFile.places.add(newPlace)
        }

        place.placesFile.save()
    }
}
