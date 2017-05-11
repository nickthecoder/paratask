package uk.co.nickthecoder.paratask.gui.field

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class FileField : LabelledField {

    override val parameter: FileParameter

    constructor (parameter: FileParameter) : super(parameter) {
        this.parameter = parameter
        control = createControl()
    }

    val textField = TextField()

    val contextMenu = ContextMenu()

    private fun createControl(): Node {

        textField.text = parameter.stringValue

        textField.textProperty().bindBidirectional(parameter.property, parameter.converter);
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        textField.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
        textField.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
        textField.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }

        textField.setContextMenu(contextMenu)

        val borderPane = BorderPane()
        borderPane.center = textField
        // TODO add the file icon, which allows dragging of the file.

        return borderPane
    }

    private fun buildContextMenu() {
        val browse = MenuItem("Browse")
        browse.setOnAction { onBrowse() }

        contextMenu.getItems().clear()
        contextMenu.getItems().addAll(browse)

        val file = parameter.value

        if (file == null) {
            return
        }

        // List siblings of current file
        val parent = file.parentFile
        if (parent != null) {
            val lister = createLister()
            val children = lister.listFiles(parent)
            if (children.size > 0) {
                contextMenu.getItems().add(SeparatorMenuItem())
                for (child in children) {
                    addMenuItem(child)
                }
            }
        }

        // List children of current directory
        if (file.isDirectory()) {
            val lister = createLister()
            val children = lister.listFiles(file)
            if (children.size > 0) {
                contextMenu.getItems().add(SeparatorMenuItem())
                for (child in children) {
                    addMenuItem(child)
                }
            }
        }
    }

    private fun createLister(): FileLister = FileLister(onlyFiles = null)

    private fun addMenuItem(file: File) {
        val menuItem = MenuItem(file.name)
        menuItem.setOnAction { setValue(file) }
        contextMenu.getItems().add(menuItem)
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

    private fun onBrowse() {
        println("Browse")
        // TODO Implement Browse
    }

    private fun onCompleteFile() {
        contextMenu.hide()
        contextMenu.getItems().clear()

        val file = parameter.value

        if (file == null) {
            return
        }

        val isDirectory = file.isDirectory
        val prefix = if (isDirectory) "" else file.name
        val lister = createLister()

        val list = lister.listFiles(if (isDirectory) file else file.parentFile)
                .filter { it.name.startsWith(prefix) }

        if (list.size == 0) {
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
        textField.text = file?.path ?: ""
        textField.positionCaret(textField.text.length)
    }
}
