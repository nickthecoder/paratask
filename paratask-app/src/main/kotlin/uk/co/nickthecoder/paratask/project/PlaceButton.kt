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

class PlaceButton(val projectWindow: ProjectWindow, val place: Place) : Button() {

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
        if (dir != null && dir.isDirectory()) {
            FileOperations.instance.fileOperation(files, dir, event.transferMode)
        }
    }

}
