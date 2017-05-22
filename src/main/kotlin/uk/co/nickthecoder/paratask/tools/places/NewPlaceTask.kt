package uk.co.nickthecoder.paratask.tools.places


class NewPlaceTask(placesFile: PlacesFile) : EditPlaceTask(URLPlace(placesFile, "", ""), name = "newPlace") {

    override fun run() {
        place.placesFile.places.add(place)
        super.run()
    }
}
