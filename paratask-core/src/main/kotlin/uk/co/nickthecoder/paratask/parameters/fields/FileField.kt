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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.parameters.FileParameter
import java.io.File

class FileField(val fileParameter: FileParameter) : FileFieldBase(fileParameter) {

    init {
        control = createControl()

        if (FileParameter.showOpenButton) {
            openButton.onAction = EventHandler { onOpen() }
            iconContainer.children.add(0, openButton)
        }
    }

    override fun buildTextField(): Node {
        super.buildTextField()
        textField.textProperty().bindBidirectional(fileParameter.valueProperty, fileParameter.converter)
        return textField
    }

    override fun getFile(): File? = fileParameter.value

    override fun setFile(file: File?) {
        fileParameter.value = file
        textField.positionCaret(textField.text.length)
    }

    fun onOpen() {

        if (fileParameter.expectFile == false) {

            val dirChooser = DirectoryChooser()
            dirChooser.title = "Choose Directory"
            fileParameter.value?.let {
                dirChooser.initialDirectory = it
            }
            val file = dirChooser.showDialog(Stage())
            file?.let { fileParameter.value = file }

        } else {

            val fileChooser = FileChooser()
            fileChooser.title = "Choose File"
            fileParameter.extensions?.let {
                it.forEach {
                    fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("*.$it", it))
                }
            }
            fileParameter.value?.let {
                if (it.isDirectory) {
                    fileChooser.initialDirectory = it
                } else {
                    fileChooser.initialDirectory = it.parentFile
                    fileChooser.initialFileName = it.name
                }
            }

            val file: File?
            if (fileParameter.mustExist == true) {
                file = fileChooser.showOpenDialog(Stage())
            } else {
                file = fileChooser.showSaveDialog(Stage())
            }
            file?.let { fileParameter.value = file }
        }
    }

}
