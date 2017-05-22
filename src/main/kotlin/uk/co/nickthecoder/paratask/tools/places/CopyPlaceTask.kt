package uk.co.nickthecoder.paratask.tools.places


class CopyPlaceTask(place: Place) : EditPlaceTask(place.copy(), "copyPlace") {

    override fun run() {
        place.placesFile.places.add(place)
        super.run()
    }
}

