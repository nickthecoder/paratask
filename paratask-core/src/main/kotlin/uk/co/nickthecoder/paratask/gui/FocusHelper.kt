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
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import java.lang.ref.WeakReference

open class FocusHelper(
        ancestor: Node,
        focusListener: FocusListener,
        val scene: Scene? = ancestor.scene,
        val name: String = "<not-named>") {

    val weakParent = WeakReference(ancestor)
    val weakFocusListener = WeakReference(focusListener)

    val changeListener = ChangeListener<Node> { _, _, newValue -> onFocusChanged(newValue) }

    init {
        //println( "Created FocusHelper ${name}")

        scene?.focusOwnerProperty()?.addListener(changeListener)

        if (scene == null) {
            debugAncestors()
        }
    }

    private fun onFocusChanged(newValue: Node?) {

        val stongAncestor = weakParent.get()
        val focusListener = weakFocusListener.get()

        if (stongAncestor == null || focusListener == null) {
            //println("Remove FocusHelper ${name} because of garbage collector.")
            remove()
        } else {

            var node: Node? = newValue

            while (node != null) {
                if (node === stongAncestor) {
                    //println("Focus changed to true for $name")
                    focusListener.focusChanged(true)
                    return
                }
                node = node.parent
            }
            //println("Focus changed to false for $name")
            focusListener.focusChanged(false)
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

    fun remove() {
        //println( "Removing FocusHelper ${name}")
        scene?.focusOwnerProperty()?.removeListener(changeListener)
    }
}

