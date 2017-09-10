/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

import javafx.scene.control.ComboBox
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.parameters.DoubleAdaptor
import uk.co.nickthecoder.paratask.parameters.ScaledDoubleParameter

/**
 */
class ScaledDoubleField(val scaledParameter: ScaledDoubleParameter, adaptor: DoubleAdaptor)

    : DoubleField(scaledParameter, adaptor) {

    val combo = ComboBox<String>()

    override fun createControl(): BorderPane {
        val border = BorderPane()
        border.center = super.createControl()
        border.right = combo

        scaledParameter.scales.forEach { key, _ ->
            combo.items.add(key)
        }

        combo.valueProperty().addListener { _, _, newValue: String ->
            scaledParameter.scaleString = newValue
        }
        combo.value = scaledParameter.scaleString

        return border
    }
}
