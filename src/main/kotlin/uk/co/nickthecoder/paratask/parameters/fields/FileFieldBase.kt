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
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.DragFiles
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

abstract class FileFieldBase(override val parameter: ValueParameter<*>) : LabelledField(parameter) {

    val textField = TextField()

    val contextMenu = ContextMenu()

    val iconContainer = HBox()

    val icon = ImageView(ParaTaskApp.imageResource("filetypes/file.png"))

    protected val borderPane = BorderPane()

    fun createControl(): Node {
        val main = buildTextField()

        DragFiles(icon) { getFile()?.let { listOf(it) } }
        DropFiles(textField, icon) { list ->
            for (file in list) {
                textField.text = file.path
                break
            }
            true
        }

        iconContainer.children.add(icon)
        iconContainer.styleClass.add("icon")
        borderPane.right = iconContainer
        borderPane.center = main
        borderPane.styleClass.add("file-field")
        icon.styleClass.add("icon")

        return borderPane
    }

    open fun buildTextField(): Node {

        with(textField) {

            text = parameter.stringValue
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
        }

        return textField
    }

    protected fun buildContextMenu() {

        val file = getFile() ?: return

        contextMenu.items.clear()
        val parent = file.parentFile
        if (parent != null) {
            // If the current value is a directory, then it will be confusing to list TWO directories.
            // in the top-level menu. So instead, we create a sub-menu
            val subMenu: Menu? = if (file.isDirectory) Menu("In ${parent.name}${File.separatorChar}") else null

            // List siblings of current file
            val children = listFiles(parent, file.name.startsWith("."))
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
            val children = listFiles(file)
            if (children.isNotEmpty()) {
                for (child in children) {
                    addMenuItem(child)
                }
            }
        }
    }

    abstract protected fun getFile(): File?

    abstract protected fun setFile(file: File?)

    protected fun listFiles(from: File, includeHidden: Boolean = false): List<File> {
        var extensions: List<String>? = null

        val param = parameter
        if (param is FileParameter) {
            param.extensions?.let {
                extensions = param.extensions
            }
        }

        val lister = FileLister(onlyFiles = null, extensions = extensions, includeHidden = includeHidden)
        return lister.listFiles(from)
    }

    protected fun addMenuItem(file: File, subMenu: Menu? = null) {
        val menuItem = MenuItem(file.name)
        menuItem.setOnAction { setFile(file) }
        menuItem.styleClass.add(if (file.isDirectory) "directory" else "file")

        if (subMenu != null) {
            subMenu.items.add(menuItem)
        } else {
            contextMenu.items.add(menuItem)
        }
    }

    private fun onKeyPressed(event: KeyEvent) {
        if (Actions.UP_DIRECTORY.match(event)) {
            getFile()?.parentFile?.let { setFile(it) }
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

        val file = getFile() ?: return

        if (file.path.startsWith("~")) {
            setFile(homeDirectory.resolve(file.path.substring(1)))
            return
        }

        val isDirectory = file.isDirectory
        val prefix = if (isDirectory) "" else file.name
        val list = listFiles(if (isDirectory) file else file.parentFile, includeHidden = file.name.startsWith("."))
                .filter { it.name.startsWith(prefix) }

        if (list.isEmpty()) {
            return
        }

        if (list.size == 1) {
            setFile(list[0])
            return
        }

        for (sister in list) {
            addMenuItem(sister)
        }
        contextMenu.show(textField, Side.BOTTOM, 0.0, 0.0)
    }

}
