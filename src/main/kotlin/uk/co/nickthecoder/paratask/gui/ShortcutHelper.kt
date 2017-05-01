package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.KeyEvent

class ShortcutHelper(val node: Node) {

    val actions = mutableListOf<Pair<Action, () -> Unit>>()

    init {
        node.addEventHandler(KeyEvent.KEY_PRESSED, { keyPressed(it) })
    }

    fun add(action: Action, func: () -> Unit) {
        actions.add(Pair(action, func))
    }

    fun keyPressed(event: KeyEvent) {
        actions.forEach { (action, func) ->
            if (action.keyCodeCombination?.match(event) == true) {
                func()
            }
        }
    }
}