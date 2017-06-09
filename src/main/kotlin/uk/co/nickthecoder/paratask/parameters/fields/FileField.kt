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

import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.layout.BorderPane
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import java.io.File

class FileField(override val parameter: FileParameter) : FileFieldBase(parameter) {

    var extensionCombo: ComboBox<String>? = null

    init {
        control = createControl()
    }

    override fun buildTextField(): Node {

        super.buildTextField()

        if (parameter.extensions == null) {
            textField.textProperty().bindBidirectional(parameter.valueProperty, parameter.converter)
        }

        parameter.extensions?.let {

            createExtensionCombo(parameter.extensions)

            textField.textProperty().bindBidirectional(parameter.valueProperty, object : StringConverter<File?>() {
                override fun toString(obj: File?): String {
                    if (obj == null) {
                        return ""
                    }
                    val path = obj.path
                    val ext = obj.extension
                    if (ext in parameter.extensions) {
                        extensionCombo!!.value = ext
                        return path.substring(0, path.length - ext.length - 1)
                    }
                    return path
                }

                override fun fromString(str: String?): File? {
                    if (str == "" || str == null) return null
                    if (str.endsWith(File.separator)) {
                        return File(str)
                    }
                    return File(str + "." + extensionCombo!!.value)
                }
            })

            val bp = BorderPane()
            bp.center = textField
            bp.right = extensionCombo
            return bp
        }

        return textField
    }

    override fun getFile(): File? = parameter.value

    override fun setFile(file: File?) {
        parameter.value = file
        textField.positionCaret(textField.text.length)
    }


    private fun createExtensionCombo(extensions: List<String>): ComboBox<String> {
        val combo = ComboBox<String>()

        extensions.forEach { combo.items.add(it) }

        val converter = object : StringConverter<String>() {
            override fun toString(obj: String): String {
                return ".$obj"
            }

            override fun fromString(str: String): String {
                return str.substring(1)
            }
        }
        combo.converter = converter
        extensionCombo = combo
        return combo
    }
}
