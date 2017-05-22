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

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.util.Labelled
import java.io.File
import java.net.URI
import java.net.URL

abstract class Place(val placesFile: PlacesFile) {

    abstract val name: String

    abstract val urlString: String

    abstract val label: String

    fun taskEdit() = EditPlaceTask(this)

    fun taskCopy() = CopyPlaceTask(this)

    fun taskRemove() = RemovePlaceTask(this)

    override fun toString() = "$urlString $label"

    abstract val icon: Image?

    abstract fun copy(): Place
}

class URLPlace(placesFile: PlacesFile, override val urlString: String, override var label: String)
    : Place(placesFile), Labelled {

    init {
        if (label == "") {
            try {
                label = URL(urlString).host
            } catch (e: Exception) {
            }
        }
    }

    override val name
        get() = urlString

    override val icon: Image? by lazy { ParaTaskApp.imageResource("filetypes/web.png") }

    override fun copy() = URLPlace(placesFile, urlString, label)
}

class FilePlace(placesFile: PlacesFile, val file: File, override var label: String) : Place(placesFile) {

    init {
        if (label == "") {
            try {
                label = file.name
            } catch (e: Exception) {
            }
        }
    }

    override val urlString = file.toURI().toURL().toString()

    override val name = file.name

    override val icon: Image? by lazy {
        ParaTaskApp.imageResource("filetypes/${if (file.isDirectory) "directory" else "file"}.png")
    }

    override fun copy() = FilePlace(placesFile, file, label)
}
