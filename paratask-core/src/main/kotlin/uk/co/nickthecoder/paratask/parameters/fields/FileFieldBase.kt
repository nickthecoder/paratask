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
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.ApplicationAction
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File


abstract class FileFieldBase(val valueParameter: ValueParameter<*>)
    : LabelledField(valueParameter) {

    val textField = TextField()

    val contextMenu = ContextMenu()

    val iconContainer = HBox()

    val icon = ImageView(ParaTask.imageResource("core/file.png"))

    val openButton = Button("…")

    protected val borderPane = BorderPane()

    override fun createControl(): BorderPane {

        val main = buildTextField()

        DragFilesHelper(icon) { getFile()?.let { listOf(it) } }
        DropFiles(textField, icon, modes = arrayOf(TransferMode.LINK)) { event ->
            for (file in event.dragboard.files) {
                textField.text = file.path
                textField.requestFocus()
                break
            }
            true
        }

        if (FileParameter.showDragIcon) {
            iconContainer.children.add(icon)
        }

        iconContainer.styleClass.add("icon")
        borderPane.right = iconContainer
        borderPane.center = main
        borderPane.styleClass.add("file-field")
        icon.styleClass.add("icon")

        return borderPane
    }

    open fun buildTextField(): TextField {

        with(textField) {

            text = valueParameter.stringValue

            addEventHandler(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
            addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
            addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }
        }
        textField.contextMenu = contextMenu

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

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        if (event.type == ParameterEventType.VALUE) {
            val error = parameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        }
    }

    override fun updateEnabled() {
        super.updateEnabled()
        textField.isDisable = !parameter.enabled
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

    val UP_DIRECTORY = ApplicationAction.createKeyCodeCombination(KeyCode.UP, alt = true)
    val COMPLETE_FILE = ApplicationAction.createKeyCodeCombination(KeyCode.DOWN, alt = true)

    private fun onKeyPressed(event: KeyEvent) {
        if (UP_DIRECTORY.match(event)) {
            getFile()?.parentFile?.let { setFile(it) }
            event.consume()
        } else if (COMPLETE_FILE.match(event)) {
            onCompleteFile()
            event.consume()
        } else if (ShortcutHelper.CONTEXT_MENU.match(event)) {
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
