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