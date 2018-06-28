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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.util.*

class ShortcutHelper(val name: String, val node: Node, val filter: Boolean = true) {

    val actions = mutableListOf<Pair<ApplicationAction, () -> Unit>>()

    val keyHandler = EventHandler<KeyEvent> { keyPressed(it) }

    init {
        enable()
    }

    fun add(action: ApplicationAction, func: () -> Unit) {
        actions.add(Pair(action, func))
    }

    fun keyPressed(event: KeyEvent) {
        try {
            actions.forEach { (action, func) ->
                if (action.keyCodeCombination?.match(event) == true) {
                    try {
                        func()
                    } catch (e: Exception) {
                        // If the event throws an exception, don't consume the event.
                        // This allows other handlers to handle the event.
                        return
                    }
                    event.consume()
                }
            }
        } catch (e: ConcurrentModificationException) {
            // Do nothing
        }
    }

    fun clear() {
        actions.clear()
    }

    fun enable() {
        if (filter) {
            node.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler)
        } else {
            node.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler)
        }
    }

    fun disable() {
        if (filter) {
            node.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler)
        } else {
            node.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler)
        }
    }

    companion object {
        val CONTEXT_MENU = ApplicationAction.createKeyCodeCombination(KeyCode.CONTEXT_MENU)
        val FOCUS_NEXT = ApplicationAction.createKeyCodeCombination(KeyCode.TAB)
        val INSERT_TAB = ApplicationAction.createKeyCodeCombination(KeyCode.TAB, control = true)

        /**
         * When called from an event handler of a shortcut, prevents the event from being consumed, so that
         * the event can be handled by another handler.
         */
        fun ignore() {
            throw Exception( "Ignore shortcut event")
        }
    }
}
