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
package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.KeyCode

object ApplicationActions {
    val SPINNER_INCREMENT = ApplicationAction("spinner.increment", KeyCode.UP)
    val SPINNER_DECREMENT = ApplicationAction("spinner.decrement", KeyCode.DOWN)
    val ENTER = ApplicationAction("enter", KeyCode.ENTER)
    val SPACE = ApplicationAction("space", KeyCode.SPACE)

    val UP_DIRECTORY = ApplicationAction("directory-up", KeyCode.UP, alt = true)
    val COMPLETE_FILE = ApplicationAction("directory-complete", KeyCode.DOWN, alt = true)

    val ITEM_ADD = ApplicationAction("item.add", KeyCode.PLUS, control = true)
    val ITEM_REMOVE = ApplicationAction("item.remove", null)
    val ITEM_UP = ApplicationAction("item.up", KeyCode.UP, control = true)
    val ITEM_DOWN = ApplicationAction("item.down", KeyCode.DOWN, control = true)
}
