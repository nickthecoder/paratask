package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription


class RemovePlaceTask(val place: Place) : AbstractTask() {
    override val taskD = TaskDescription("deleteTask", description = "Remove ${place.label} (${place.urlString}")

    override fun run() {
        place.placesFile.places.remove(place)
        place.placesFile.save()
    }
}