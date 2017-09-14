package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.util.Resource
import java.io.Serializable

class PlaceInFile(
        @Transient val placesFile: PlacesFile,
        resource: Resource,
        label: String)

    : Place(resource, label), Serializable {


    fun taskEdit() = EditPlaceTask(this)

    fun taskCopy() = CopyPlaceTask(this)

    fun taskRemove() = RemovePlaceTask(this)

}
