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

import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class ShortcutHelper(val name: String, val node: Node, val filter: Boolean = true) {

    val actions = mutableListOf<Pair<ApplicationAction, () -> Unit>>()

    init {
        if (filter) {
            node.addEventFilter(KeyEvent.KEY_PRESSED, { keyPressed(it) })
        } else {
            node.addEventHandler(KeyEvent.KEY_PRESSED, { keyPressed(it) })
        }
    }

    fun add(action: ApplicationAction, func: () -> Unit) {
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

    fun clear() {
        actions.clear()
    }

    companion object {
        val CONTEXT_MENU = ApplicationAction.createKeyCodeCombination(KeyCode.CONTEXT_MENU)
        val FOCUS_NEXT = ApplicationAction.createKeyCodeCombination(KeyCode.TAB)
        val INSERT_TAB = ApplicationAction.createKeyCodeCombination(KeyCode.TAB, control = true)

    }
}
