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

package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.DragEvent
import uk.co.nickthecoder.paratask.gui.MyTab

abstract class MinorTab(text: String = "", content: Node = Label("Empty"), graphic: Node? = null)
    : MyTab(text, content, graphic) {

    abstract fun selected()

    abstract fun deselected()

    abstract fun focus()

    private var dragExited: Boolean = true

    init {

        // Select the tab when a drag is over it. This will allow the item(s) to be dropped on nodes in the tab's contents
        // which would otherwise be unavailable (because another tab's content was visible).
        addEventHandler(DragEvent.DRAG_ENTERED) {
            Thread(Runnable {
                dragExited = false
                Thread.sleep(500)
                if (!dragExited) {
                    Platform.runLater {
                        isSelected = true
                    }
                }
            }).start()
        }
        addEventHandler(DragEvent.DRAG_EXITED) {
            dragExited = true
        }
    }
}
