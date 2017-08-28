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

import javafx.scene.input.DragEvent

class CompoundDropHelper(vararg helpers: SimpleDropHelper<*>) : AbstractDropHelper() {

    val dropHelpers = helpers.toMutableList()

    var currentHelper: SimpleDropHelper<*>? = null

    override fun onDragOver(event: DragEvent) : Boolean {
        currentHelper?.let {
            return it.onDragOver(event)
        }

        dropHelpers.forEach {
            if (it.onDragOver(event)) {
                currentHelper = it
                return true
            }
        }

        currentHelper = null
        return false
    }

    override fun onDragExited(event: DragEvent) {
        currentHelper?.onDragExited(event)
        currentHelper = null
    }

    override fun onDragDropped(event: DragEvent) {
        currentHelper?.onDragDropped(event)
    }

}
