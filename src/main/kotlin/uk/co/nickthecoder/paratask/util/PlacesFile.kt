package uk.co.nickthecoder.paratask.util

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.ParameterEvent
import uk.co.nickthecoder.paratask.parameter.ParameterListener
import uk.co.nickthecoder.paratask.parameter.StringParameter
import java.io.File
import java.net.URI
import java.net.URL

class PlacesFile(override val file: File) : HasFile {

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
                val filePart = File(url.getFile())
                label = filePart.name
            } catch (e: Exception) {
                label = ""
            }
        }

        // TODO Handle files relative to this places file. e.g.  relative:foo/bar%20bar/baz
        val url = URL(urlString)
        if (urlString.startsWith("file:")) {
            val file = File(url.toURI())
            return FilePlace(file, label)
        } else {
            return URLPlace(urlString, label)
        }
    }

    fun taskNew(): EditPlaceTask = NewPlaceTask()

    abstract inner class Place() {

        abstract val urlString: String
        abstract val label: String


        fun taskEdit() = EditPlaceTask(this)

        fun taskCopy() = CopyPlaceTask(this)

        fun taskRemove() = RemovePlaceTask(this)

        override fun toString() = "${urlString} ${label}"

        abstract val icon: Image?

        abstract fun copy(): Place
    }

    inner open class URLPlace(override val urlString: String, override var label: String)
        : Place(), Labelled {

        init {
            if (label == "") {
                try {
                    label = URL(urlString).getHost()
                } catch (e: Exception) {
                }
            }
        }

        val uri by lazy { URI(urlString) }

        override val icon: Image? by lazy { ParaTaskApp.imageResource("filetypes/web.png") }

        override open fun copy() = URLPlace(urlString, label)
    }

    inner class FilePlace(override val file: File, override var label: String)
        : Place(), HasFile {

        init {
            if (label == "") {
                try {
                    label = file.name
                } catch (e: Exception) {
                }
            }
        }

        override val urlString = file.toURI().toURL().toString()

        override val icon: Image? by lazy {
            ParaTaskApp.imageResource("filetypes/${if (file.isDirectory) "directory" else "file"}.png")
        }

        override fun copy() = FilePlace(file, label)
    }


    inner open class EditPlaceTask(protected val place: Place, name: String = "editPlace")
        : AbstractTask(), ParameterListener {

        override val taskD = TaskDescription(name)

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

            url.parameterListeners.add(this)
            file.parameterListeners.add(this)
        }

        override fun parameterChanged(event: ParameterEvent) {
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
                URLPlace(url.value, label.value)
            } else {
                FilePlace(file.value!!, label.value)
            }
        }

        override fun check() {
            super.check()
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
            val index = places.indexOf(place)
            places.remove(place)

            val newPlace = createPlace()
            if (index >= 0) {
                places.add(index, newPlace)
            } else {
                places.add(newPlace)
            }

            save()
        }
    }


    inner class CopyPlaceTask(place: Place) : EditPlaceTask(place.copy(), "copyPlace") {

        override fun run() {
            places.add(place)
            super.run()
        }
    }


    inner class NewPlaceTask() : EditPlaceTask(URLPlace("", ""), name = "newPlace") {

        override fun run() {
            places.add(place)
            super.run()
        }
    }


    inner class RemovePlaceTask(val place: Place) : AbstractTask() {
        override val taskD = TaskDescription("deleteTask", description = "Remove ${place.label} (${place.urlString}")

        override fun run() {
            places.remove(place)
            save()
        }
    }

}

