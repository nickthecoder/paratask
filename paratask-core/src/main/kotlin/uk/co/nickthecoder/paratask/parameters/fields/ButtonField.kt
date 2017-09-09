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

import javafx.event.ActionEvent
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.parameters.ButtonParameter

class ButtonField(val buttonParameter: ButtonParameter)
    : ParameterField(buttonParameter) {

    var button: Button? = null

    override fun createControl(): Button {
        button = Button(buttonParameter.buttonText)
        button!!.addEventHandler(ActionEvent.ACTION) { buttonParameter.action(this) }
        return button!!
    }
}
