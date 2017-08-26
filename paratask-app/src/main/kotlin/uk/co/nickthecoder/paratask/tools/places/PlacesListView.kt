package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.control.ListView
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import java.io.File

class PlacesListView(placesFile: PlacesFile) : ListView<Place>() {

    var onSelected: ((File) -> Unit)? = null

    val selectedDirectory: File?
        get() = selectionModel.selectedItem.file

    init {
        setCellFactory { PlaceListCell() }

        placesFile.places.filter { it.isDirectory() }.forEach {
            items.add(it)
        }

        addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

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


