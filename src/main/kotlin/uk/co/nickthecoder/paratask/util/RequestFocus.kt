package uk.co.nickthecoder.paratask.util

import javafx.scene.Node
import javafx.scene.Parent

/**
 * @param attempts - In case there is a bug, prevent infinite loops by stopping after checking this many nodes.
 */
fun Node.focusNext(attempts: Int = 1000) {
    findNextFocus(attempts)?.let { RequestFocus.requestFocus(it) }
}

fun Node.findNextFocus(attempts: Int = 1000): Node? {
    val fn = RequestFocus(this, attempts)
    if (this is Parent) {
        fn.findInsideParent(this)?.let { return it }
    }
    return fn.findFromNode(this)
}

class RequestFocus(val startNode: Node, var attempts: Int) {

    fun findInsideParent(parent: Parent): Node? {
        if (--attempts <= 0) return startNode

        for (child in parent.childrenUnmodifiable) {
            findFocusNode(child)?.let { return it }
        }

        return null
    }

    fun findFocusNode(node: Node): Node? {
        if (debug) println("Trying node ${node}")
        if (--attempts <= 0) return startNode

        if (node === startNode) {
            return startNode
        }

        if (node.isFocusTraversable) {
            if (debug) println("Success! ${node}")
            return node // Success!
        }
        if (node is Parent) {
            if (debug) println("Going inside parent node ${node}")
            findInsideParent(node)?.let { return it }
        }
        return null
    }


    fun findFromNode(node: Node): Node? {
        val parent = node.parent
        if (parent == null) return null

        val children = parent.childrenUnmodifiable
        val idx = children.indexOf(node)
        if (idx >= 0) {
            if (debug) println("Trying all siblings after ${node}")
            for (i in idx + 1..children.size - 1) {
                findFocusNode(children[i])?.let { return it }
            }
            if (debug) println("Done all siblings after ${node}")
        }
        // We have looked at all the later siblings and their descendants

        // Now look at PARENT's later siblings
        if (debug) println("Trying from parent node ${parent}")
        findFromNode(parent)?.let { return it }

        if (parent.parent == null) {
            // We've looked at all the LATER nodes, lets look at the earlier nods
            if (debug) println("Trying ealier nodes")
            for (i in 0..idx - 1) {
                findFocusNode(children[i])?.let { return it }
            }
            if (debug) println("Not in earlier nodes either. Damn!")
        }

        return null
    }

    companion object {
        var debug: Boolean = false

        fun requestFocus(node: Node) {
            if (debug) println("Requesting focus on ${node}")
            node.requestFocus()
        }
    }

}

