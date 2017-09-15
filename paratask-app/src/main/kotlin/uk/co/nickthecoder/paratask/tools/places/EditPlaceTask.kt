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
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.compound.ResourceParameter

open class EditPlaceTask(protected val place: PlaceInFile, name: String = "editPlace")
    : AbstractTask() {

    final override val taskD = TaskDescription(name)

    val labelP = StringParameter("label", required = false, value = place.label)

    val resourceP = ResourceParameter("resource", value = place.resource)
    var resource by resourceP

    init {
        taskD.addParameters(labelP, resourceP)
    }

    override fun run() {
        val index = place.placesFile.places.indexOf(place)
        place.placesFile.places.remove(place)

        val newPlace = PlaceInFile(place.placesFile, resource!!, labelP.value)
        if (index >= 0) {
            place.placesFile.places.add(index, newPlace)
        } else {
            place.placesFile.places.add(newPlace)
        }

        place.placesFile.save()
    }
}
