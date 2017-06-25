package uk.co.nickthecoder.paratask.util

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

/**
 * A part of MyTabPane.
 * Unlike the standard JavaFX Tab, MyTab is a Node
 */
open class MyTab(content: Node, text: String, graphic: Node? = null) : BorderPane() {

    internal var parent: MyTabPane? = null

    var label = Label(text)

    var graphic: Node? = graphic

    val closeButton = Button("")

    var content: Node = content
        set(v) {
            parent?.let { parent ->
                if (parent.currentTab === this) {
                    // Ensure that the new content is displayed
                    parent.currentTab = this
                }
            }
        }

    init {
        styleClass.add("tab")
        label.styleClass.add("tab-label")
        closeButton.styleClass.add("tab-close-button")
        graphic?.let { children.add(it) }
        center = label
        right = closeButton
        left = graphic
        addEventHandler(MouseEvent.MOUSE_PRESSED) { parent?.let { it.currentTab = this } }
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED) { close() }
    }

    open fun close() {
        parent?.remove(this)
    }
}
