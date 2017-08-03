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
import uk.co.nickthecoder.paratask.parameters.FileParameter
import java.io.File

class FileField(override val parameter: FileParameter) : FileFieldBase(parameter) {

    init {
        control = createControl()
    }

    override fun buildTextField(): Node {
        super.buildTextField()
        textField.textProperty().bindBidirectional(parameter.valueProperty, parameter.converter)
        return textField
    }

    override fun getFile(): File? = parameter.value

    override fun setFile(file: File?) {
        parameter.value = file
        textField.positionCaret(textField.text.length)
    }
}
