package uk.co.nickthecoder.paratask.gui

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