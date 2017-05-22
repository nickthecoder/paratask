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

package uk.co.nickthecoder.paratask.gui

import javafx.scene.control.Button
import javafx.scene.layout.HBox

/**
 * Used to style buttons so that they are joined together, with rounded corners on the first and
 * last buttons only.
 *
 * Note, this class isn't well developed. It cannot handle removing buttons, not making the first/last buttons
 * invisible.
 */
class ButtonGroup : HBox() {

    init {
        styleClass.add("button-group")
    }

    fun add(button: Button) {

        if (children.size == 0) {
            button.styleClass.add("first")
        } else {
            if (children.size > 1) {
                children[children.count() - 1].styleClass.add("middle")
                children[children.count() - 1].styleClass.remove("last")
            }
            button.styleClass.add("last")
        }
        children.add(button)
    }
}
