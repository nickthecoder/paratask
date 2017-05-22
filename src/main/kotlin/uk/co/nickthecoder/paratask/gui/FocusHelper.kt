package uk.co.nickthecoder.paratask.gui

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import java.lang.ref.WeakReference

class FocusHelper(parent: Node, focusListener: FocusListener, val scene: Scene? = parent.scene, val name: String = "<not-named>")
    : ChangeListener<Node> {

    val weakParent = WeakReference(parent)
    val weakFocusListener = WeakReference(focusListener)

    init {
        //println( "Created FocusHelper ${name}")
        scene?.focusOwnerProperty()?.addListener(this)

        if (scene == null) {
            debugAncestors()
        }
    }

    fun debugAncestors() {
        Thread.dumpStack()
        println("Focus Listener could not find the Scene. Ancestors :")
        var node: Node? = weakParent.get()
        while (node != null) {
            println(node)
            node = node.parent
        }
        println()
    }

    override fun changed(observable: ObservableValue<out Node>?, oldValue: Node?, newValue: Node?) {
        var n: Node? = newValue

        val parent = weakParent.get()
        val focusListener = weakFocusListener.get()

        if (parent == null || focusListener == null) {
            //println("Remove FocusHelper ${name} because of garbage collector.")
            remove()
            return
        }

        while (n != null) {
            if (n === parent) {
                focusListener.focusChanged(true)
                return
            }
            n = n.parent
        }
        focusListener.focusChanged(false)
    }

    fun remove() {
        //println( "Removing FocusHelper ${name}")
        scene?.focusOwnerProperty()?.removeListener(this)
    }
}