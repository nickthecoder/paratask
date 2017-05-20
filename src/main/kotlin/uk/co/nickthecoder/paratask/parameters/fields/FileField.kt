package uk.co.nickthecoder.paratask.parameters.fields

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.gui.DragFiles
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class FileField(override val parameter: FileParameter) : LabelledField(parameter) {

    val textField = TextField()

    val icon = ImageView(ParaTaskApp.imageResource("filetypes/file.png"))

    val contextMenu = ContextMenu()

    private fun createControl(): Node {

        with(textField) {
            text = parameter.stringValue
            textProperty().bindBidirectional(parameter.valueProperty, parameter.converter)
            textProperty().addListener({ _, _, _: String ->
                val error = parameter.errorMessage()
                if (error == null) {
                    clearError()
                } else {
                    showError(error)
                }
            })

            addEventHandler(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
            addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
            addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }

            contextMenu = contextMenu
        }

        DragFiles(icon) { parameter.value?.let { listOf(it) } }
        DropFiles(textField, icon) { list ->
            for (file in list) {
                textField.text = file.path
                break
            }
            true
        }

        val borderPane = BorderPane()
        borderPane.center = textField
        borderPane.right = icon

        return borderPane
    }

    private fun buildContextMenu() {

        contextMenu.items.clear()
        //val browse = MenuItem("Browse")
        //browse.setOnAction { onBrowse() }
        //contextMenu.getItems().addAll(browse)

        val file = parameter.value ?: return

        val parent = file.parentFile
        if (parent != null) {
            // If the current value is a directory, then it will be confusing to list TWO directories.
            // in the top-level menu. So instead, we create a sub-menu
            val subMenu: Menu? = if (file.isDirectory) Menu("In ${parent.name}${File.separatorChar}") else null
            // List siblings of current file
            val lister = createLister()
            val children = lister.listFiles(parent)
            if (children.isNotEmpty()) {
                if (subMenu != null) {
                    contextMenu.items.add(subMenu)
                }
                for (child in children) {
                    addMenuItem(child, subMenu)
                }
            }
        }

        // List children of current directory
        if (file.isDirectory) {
            val lister = createLister()
            val children = lister.listFiles(file)
            if (children.isNotEmpty()) {
                for (child in children) {
                    addMenuItem(child)
                }
            }
        }
    }

    private fun createLister(): FileLister = FileLister(onlyFiles = null)

    private fun addMenuItem(file: File, subMenu: Menu? = null) {
        val menuItem = MenuItem(file.name)
        menuItem.setOnAction { setValue(file) }
        menuItem.styleClass.add(if (file.isDirectory) "directory" else "file")

        if (subMenu != null) {
            subMenu.items.add(menuItem)
        } else {
            contextMenu.items.add(menuItem)
        }
    }

    private fun onKeyPressed(event: KeyEvent) {
        if (Actions.UP_DIRECTORY.match(event)) {
            setValue(parameter.value?.parentFile)
            event.consume()
        } else if (Actions.COMPLETE_FILE.match(event)) {
            onCompleteFile()
            event.consume()
        } else if (Actions.CONTEXT_MENU.match(event)) {
            onContextMenu()
            event.consume()
        }
    }

    private fun onMouse(event: MouseEvent) {
        if (event.isPopupTrigger) {
            buildContextMenu()
            event.consume()
        }
    }

    private fun onContextMenu() {
        buildContextMenu()
        contextMenu.show(textField, Side.BOTTOM, 0.0, 0.0)
    }

    private fun onCompleteFile() {
        contextMenu.hide()
        contextMenu.items.clear()

        val file = parameter.value ?: return

        val isDirectory = file.isDirectory
        val prefix = if (isDirectory) "" else file.name
        val lister = createLister()

        val list = lister.listFiles(if (isDirectory) file else file.parentFile)
                .filter { it.name.startsWith(prefix) }

        if (list.isEmpty()) {
            return
        }

        if (list.size == 1) {
            setValue(list[0])
            return
        }

        for (sister in list) {
            addMenuItem(sister)
        }
        contextMenu.show(textField, Side.BOTTOM, 0.0, 0.0)
    }

    private fun setValue(file: File?) {
        val suffix = if (file?.isDirectory ?: false) File.separator else ""
        textField.text = file?.path + suffix
        textField.positionCaret(textField.text.length)
    }

    init {
        control = createControl()
    }
}
