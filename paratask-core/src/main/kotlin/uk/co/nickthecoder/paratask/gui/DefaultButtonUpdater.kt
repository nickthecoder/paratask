/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

//private var instanceCount = 0

/**
 * Changes the "default" property of a Button when the focus is somewhere within one of its ancestor nodes.
 * This allows multiple "default" buttons within a single Scene, and their "default" property will change
 * appropriately.
 *
 * Note, we use WeakReferences, to prevent memory leaks without having to manually remove the focus listener.
 * This will NOT prevent the Button nor its ancestor being garbage collected, and the listener will be removed
 * from the scene once the button or the ancestor ARE garbage collected.
 */
class DefaultButtonUpdater(button: Button, ancestor: Node, val name: String = "<not known>") {

    val weakParent = WeakReference(ancestor)
    val weakButton = WeakReference(button)

    val changeListener = ChangeListener<Node> { _, _, newValue -> onFocusChanged(newValue) }

    var weakScene: WeakReference<Scene>? = null

    init {
        // println("Created DefaultButtonUpdater $name $instanceCount")
        // instanceCount++

        // Some controls, such as Toolbar and TabPane do NOT update the parent or scene of child nodes straight away.
        // I think this is all parents that use "items" rather than "children" to manage their child nodes.
        // And it is realted to how the control's Skin is initialised later than you might expect.
        // This code listens for when the scene is set, and adds the focusOwner listener then.
        val sceneNow = button.scene
        if (sceneNow == null) {
            button.sceneProperty().addListener { _, _, newScene ->
                if (newScene != null) {
                    newScene.focusOwnerProperty().addListener(changeListener)
                    weakScene = WeakReference(newScene)
                }
            }
        } else {
            sceneNow.focusOwnerProperty().addListener(changeListener)
            weakScene = WeakReference(sceneNow)
        }
    }


    private fun onFocusChanged(newValue: Node?) {

        val ancestor = weakParent.get()

        if (ancestor == null) {
            remove()
        } else {

            var node: Node? = newValue

            while (node != null) {
                if (node === ancestor) {
                    //println("Focus changed to true for $name")
                    focusChanged(true)
                    return
                }
                node = node.parent
            }
            //println("Focus changed to false for $name")
            focusChanged(false)
        }
    }

    fun remove() {
        // println("Removing DefaultButtonUpdater ${name}")
        weakScene?.get()?.focusOwnerProperty()?.removeListener(changeListener)
    }

    private fun focusChanged(gained: Boolean) {
        val button = weakButton.get()
        if (button == null) {
            remove()
        } else {
            button.isDefaultButton = gained
        }
    }

    //protected fun finalize() {
    //    instanceCount--
    //    println("Finilizing DefautButtonUpdater $name #$instanceCount")
    //}
}

fun Button.defaultWhileFocusWithin(ancestor: Node, name: String = "<not named>"): DefaultButtonUpdater {
    return DefaultButtonUpdater(this, ancestor, name = name)
}
