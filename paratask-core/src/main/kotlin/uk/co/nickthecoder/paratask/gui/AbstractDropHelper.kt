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

import javafx.scene.Node
import javafx.scene.input.DragEvent

abstract class AbstractDropHelper : DropHelper {

    var targets = mutableSetOf<Node>()

    var excludes = mutableSetOf<Node>()

    var debug = false

    fun debugPrintln(str: String) {
        if (debug) {
            println(str)
        }
    }

    override fun applyTo(target: Node): AbstractDropHelper {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }

        return this
    }

    override fun unapplyTo(target: Node) {
        target.onDragOver = null
        target.onDragExited = null
        target.onDragDropped = null
    }

    override fun exclude(node: Node): AbstractDropHelper {
        excludes.add(node)
        return this
    }

    override fun cancel() {
        targets.forEach {
            it.onDragOver = null
            it.onDragExited = null
            it.onDragDropped = null
        }
    }

}
