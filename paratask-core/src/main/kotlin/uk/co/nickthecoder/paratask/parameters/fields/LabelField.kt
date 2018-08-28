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

import javafx.scene.Node
import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.parameters.LabelParameter

/**
 * Renders as a single plain piece of text.
 * I created this for adding an "x" between width and height parameters in a horizontal group.
 *
 * If we compare a LabelField to a StringField, then the LabelField has a Label instead of the TextField.
 * A StringParameter also has a Label (usually to the left of the TextField),
 * but a LabelParameter has nothing in that position. i.e. hasLabel = false.
 * This makes it behave correctly when part of a HorizontalGroupField, and makes no difference when part of a
 * column based form.
 */
class LabelField(val labelParameter: LabelParameter) : ParameterField(labelParameter) {

    override val hasLabel = false

    override fun createControl(): Node {
        return Label(labelParameter.label)
    }
}
