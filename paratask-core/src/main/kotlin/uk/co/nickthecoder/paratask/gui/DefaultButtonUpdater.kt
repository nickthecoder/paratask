package uk.co.nickthecoder.paratask.gui

import javafx.beans.value.ChangeListener
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import java.lang.ref.WeakReference

private var instanceCount = 0

class DefaultButtonUpdater(button: Button, ancestor: Node, val name: String = "<not known>", val scene: Scene? = button.scene) {

    val weakParent = WeakReference(ancestor)
    val weakButton = WeakReference(button)

    val changeListener = ChangeListener<Node> { _, _, newValue -> onFocusChanged(newValue) }

    init {
        println("Created DfaultButtonUpdater $name $instanceCount")
        instanceCount++
        scene?.focusOwnerProperty()?.addListener(changeListener)

        if (scene == null) {
            debugAncestors()
        }
    }

    private fun onFocusChanged(newValue: Node?) {

        val ancestor = weakParent.get()
        //println("DBU.onFocusChanged $ancestor")

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

    fun debugAncestors() {
        Thread.dumpStack()
        println("Focus Listener could not find the Scene for $name. Ancestors :")
        var node: Node? = weakParent.get()
        while (node != null) {
            println(node)
            node = node.parent
        }
        println()
    }

    fun remove() {
        println("Removing DefaultButtonUpdater ${name}")
        scene?.focusOwnerProperty()?.removeListener(changeListener)
    }

    private fun focusChanged(gained: Boolean) {
        val button = weakButton.get()
        if (button == null) {
            remove()
        } else {
            button.isDefaultButton = gained
        }
    }

    protected fun finalize() {
        instanceCount--
        println("Finilizing DefautButtonUpdater $name #$instanceCount")
    }
}

fun Button.defaultWhileFocusWithin(ancestor: Node, name: String = "<not named>", scene: Scene = this.scene): DefaultButtonUpdater {
    return DefaultButtonUpdater(this, ancestor, name = name, scene = scene)
}
