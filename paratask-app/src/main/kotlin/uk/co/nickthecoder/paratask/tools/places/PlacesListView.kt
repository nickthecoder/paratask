package uk.co.nickthecoder.paratask.tools.places

import javafx.application.Platform
import javafx.scene.control.ListView
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.*
import uk.co.nickthecoder.paratask.misc.AutoRefresh
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File

class PlacesListView(file: File) : ListView<Place>() {

    var onSelected: ((File) -> Unit)? = null

    val selectedDirectory: File?
        get() = selectionModel.selectedItem.file


    var placesFile = PlacesFile(file)

    val placesDropHelper = SimpleDropHelper<List<Place>>(Place.dataFormat, arrayOf(TransferMode.COPY, TransferMode.MOVE)) { _, content ->

        content.forEach {
            placesFile.places.add(Place(placesFile, it.resource, it.label))
        }
        placesFile.save()
    }

    val filesDropHelper = DropFiles(arrayOf(TransferMode.LINK)) { _, content ->
        for (f in content) {
            placesFile.places.add(Place(placesFile, Resource(f), f.name))
        }
        placesFile.save()
    }

    val dropHelper = CompoundDropHelper(placesDropHelper, filesDropHelper)

    val dragHelper = DragFilesHelper(allowMove = false, allowCopy = false) {
        val dir = selectedDirectory
        if (dir == null) {
            null
        } else {
            listOf(dir)
        }
    }

    val autoRefresh = AutoRefresh {
        Platform.runLater {
            placesFile = PlacesFile(file)
            buildList()
        }
    }

    init {
        autoRefresh.watch(file)

        setCellFactory { PlaceListCell() }

        buildList()

        addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

        dropHelper.applyTo(this)
        dragHelper.applyTo(this)
    }

    fun buildList() {
        items.clear()
        placesFile.places.filter { it.isDirectory() }.forEach {
            items.add(it)
        }
    }

    fun onMouseClicked(event: MouseEvent) {
        if (event.clickCount == 2) {
            onSelect()
            event.consume()
        }
    }

    fun onKeyPressed(event: KeyEvent) {
        if (ApplicationActions.ENTER.match(event)) {
            onSelect()
            event.consume()
        }
    }

    fun onSelect() {
        onSelected?.let { handler ->
            selectedDirectory?.let {
                handler(it)
            }
        }
    }


    class PlaceListCell : TextFieldListCell<Place>() {
        init {
            converter = object : StringConverter<Place>() {
                override fun toString(place: Place?): String {
                    return place?.label ?: ""
                }

                override fun fromString(str: String): Place? = null
            }

        }

        override fun updateItem(item: Place?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null && !empty) {
                graphic = ImageView(ParaTask.imageResource("filetypes/directory.png"))
            }
        }
    }
}


