package uk.co.nickthecoder.paratask.gui

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.util.getScene
import uk.co.nickthecoder.paratask.util.getParentBodge

class FocusListener(val parent: Node, val callback: (Boolean) -> Unit)
    : ChangeListener<Node> {

    val scene = getScene(parent)

    init {
        scene?.focusOwnerProperty()?.addListener(this)

        if (scene == null) {
            debugAncestors()
        }
    }

    fun debugAncestors() {
        Thread.dumpStack()
        println("*** Focus Listener could not find the Scene. Ancestors :")
        var node: Node? = parent
        while (node != null) {
            println(node)
            node = node.getParentBodge()
        }
        println()
    }

    override fun changed(observable: ObservableValue<out Node>?, oldValue: Node?, newValue: Node?) {
        var n: Node? = newValue

        while (n != null) {
            if (n === parent) {
                callback(true)
                return
            }
            n = n.parent
        }
        callback(false)
    }

    fun remove() {
        scene?.focusOwnerProperty()?.removeListener(this)
    }
}