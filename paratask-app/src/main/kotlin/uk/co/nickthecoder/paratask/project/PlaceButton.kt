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
package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.tools.places.DirectoryTool
import uk.co.nickthecoder.paratask.tools.places.Place
import java.io.File

open class PlaceButton(val projectWindow: ProjectWindow, val place: Place) : Button() {

    init {
        onAction = EventHandler { onAction() }

        graphic = ImageView(place.resource.icon)
        text = place.label
        tooltip = Tooltip(place.path)

        if (place.isDirectory()) {
            DropFiles(TransferMode.ANY) { event, files -> onDroppedFiles(event, files) }.applyTo(this)
        }
    }

    fun onAction() {
        if (place.isDirectory()) {
            val dt = DirectoryTool()
            dt.directoriesP.value = listOf(place.file)
            projectWindow.tabs.addTool(dt)
        }
    }

    fun onDroppedFiles(event: DragEvent, files: List<File>) {
        val dir = place.file
        if (dir != null && dir.isDirectory) {
            FileOperations.instance.fileOperation(files, dir, event.transferMode)
        }
    }

}
