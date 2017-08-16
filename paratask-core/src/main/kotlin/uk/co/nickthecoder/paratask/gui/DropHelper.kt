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

import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

open class DropHelper<T>(
        val dataFormat: DataFormat,
        modes: Array<TransferMode> = TransferMode.ANY,
        val dropped: ((DragEvent) -> Boolean)? = null

) : AbstractDropHelper(modes = modes) {


    override fun accept(event: DragEvent): Array<TransferMode>? {
        return if (event.gestureSource !== source && event.dragboard.hasContent(dataFormat)) modes else null
    }

    override fun onDropped(event: DragEvent): Boolean {
        dropped?.let {
            return it(event)
        }
        return false
    }

    fun content(event: DragEvent): T {
        @Suppress("UNCHECKED_CAST")
        return event.dragboard.getContent(dataFormat) as T
    }
}
