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
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.parameters.ResourceParameter
import java.io.File

class ResourceField(val resourceParameter: ResourceParameter) :FileFieldBase(resourceParameter) {

    override fun createControl(): BorderPane {
        return super.createControl()
    }

    override fun buildTextField() : TextField {
        with(textField) {
            textProperty().bindBidirectional(resourceParameter.valueProperty, resourceParameter.converter)
        }
        return super.buildTextField()
    }


    override fun getFile(): File? = resourceParameter.value?.file

    override fun setFile(file: File?) {
        val suffix = if (file?.isDirectory ?: false) File.separator else ""
        textField.text = file?.path + suffix
        textField.positionCaret(textField.text.length)
    }

}
