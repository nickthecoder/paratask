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
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.Resource

open class EditPlaceTask(protected val place: Place, name: String = "editPlace")
    : AbstractTask() {

    final override val taskD = TaskDescription(name)

    val labelP = StringParameter("label", required = false)

    val fileP = FileParameter("file", expectFile = null, required = false)

    val urlP = StringParameter("url", required = false)

    val oneOfP = OneOfParameter("fileOrURL")

    init {
        taskD.addParameters(labelP, oneOfP)
        oneOfP.addParameters(fileP, urlP)

        if (place.resource.isFile()) {
            fileP.value = place.resource.file!!
        } else {
            urlP.value = place.resource.toString()
        }
        labelP.value = place.label

        urlP.listen { parameterChanged(it) }
        fileP.listen { parameterChanged(it) }
    }

    fun parameterChanged(event: ParameterEvent) {
        if (event.parameter === fileP) {
            if (fileP.value != null) {
                urlP.value = ""
            }
        }
        if (event.parameter === urlP) {
            if (urlP.value != "") {
                fileP.value = null
            }
        }
    }


    private fun createPlace(): Place {
        return if (fileP.value == null) {
            Place(place.placesFile, Resource(urlP.value), labelP.value)
        } else {
            Place(place.placesFile, Resource(fileP.value!!), labelP.value)
        }
    }

    override fun customCheck() {
        if (fileP.value == null && (urlP.value == "")) {
            throw ParameterException(urlP, "You must enter a URL or a File")
        }
        try {
            createPlace()
        } catch(e: Exception) {
            throw ParameterException(urlP, "Invlid URL")
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
