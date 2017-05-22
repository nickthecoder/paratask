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

import javafx.scene.Node
import javafx.scene.input.KeyEvent

class ShortcutHelper(val name: String, val node: Node) {

    val actions = mutableListOf<Pair<Action, () -> Unit>>()

    init {
        node.addEventFilter(KeyEvent.KEY_PRESSED, { keyPressed(it) })
    }

    fun add(action: Action, func: () -> Unit) {
        actions.add(Pair(action, func))
    }

    fun keyPressed(event: KeyEvent) {
        actions.forEach { (action, func) ->
            if (action.keyCodeCombination?.match(event) == true) {
                event.consume()
                func()
            }
        }
    }
}