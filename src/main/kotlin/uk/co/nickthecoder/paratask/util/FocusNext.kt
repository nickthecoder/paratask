package uk.co.nickthecoder.paratask.util

import javafx.scene.Node
import javafx.scene.Parent

/**
 * @param attempts - In case there is a bug, prevent infinite loops by stopping after checking this many nodes.
 */
fun Node.focusNext(attempts: Int = 1000) {
    FocusNext(this, attempts).tryFromNode(this)
}

private class FocusNext(val startNode: Node, var attempts: Int) {

    // true - stop
    // false - carry on looking

    fun focusInsideParent(parent: Parent): Boolean {
        if (--attempts <= 0) return true

        for (child in parent.childrenUnmodifiable) {
            if (tryFocusNode(child)) {
                return true
            }
        }

        return false
    }

    fun tryFocusNode(node: Node): Boolean {
        if (--attempts <= 0) return true
        if (node === startNode) {
            attempts = 0
            return true
        }

        if (node.isFocusTraversable) {
            node.requestFocus()
            return true
        }
        if (node is Parent) {
            if (focusInsideParent(node)) {
                return true
            }
        }
        return false
    }

    fun tryFromNode(node: Node): Boolean {
        val parent = node.parent
        if (parent == null) return false

        val children = parent.childrenUnmodifiable
        val idx = children.indexOf(node)
        if (idx >= 0) {
            // We have looked at all the siblings AFTER node
            for (i in idx + 1..children.size - 1) {
                if (tryFocusNode(children[i])) {
                    return true
                }
            }
            // We have looked at all the later siblings and their descendants
            val parent = node.parent

            if (parent == null) {
                // TODO - Bug it will never get here! (plus parent is shadowed)
                // We've reached the top of the tree. Look at ealier siblings
                for (i in 0..idx - 1) {
                    if (tryFocusNode(children[i])) {
                        return true
                    }
                }

            } else {
                // Now look at PARENT's later siblings
                if (tryFromNode(parent)) {
                    return true
                }
            }
        }
        return false
    }
}
