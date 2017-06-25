package uk.co.nickthecoder.paratask.util

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane

/**
 * A part of MyTabPane.
 * Unlike the standard JavaFX Tab, MyTab is a Node
 */
open class MyTab(content: Node = Label("Empty"), text: String = "", graphic: Node? = null) : BorderPane() {

    internal var tabPane: MyTabPane? = null

    var label = Label(text)

    var graphic: Node?
        set(v) {
            label.graphic = v
        }
        get() = label.graphic

    val closeButton = Button("")

    var content: Node = content
        set(v) {
            tabPane?.let {
                it.contents.children.remove(field)
                it.contents.children.add(v)
                if (it.selectedTab === this) {
                    // Ensure that the new content is displayed
                    it.selectedTab = this
                }
            }
            field = v
        }

    init {
        this.graphic = graphic
        styleClass.add("tab")
        label.styleClass.add("tab-label")
        closeButton.styleClass.add("tab-close-button")
        graphic?.let { children.add(it) }
        center = label
        right = closeButton
        addEventHandler(MouseEvent.MOUSE_PRESSED) { tabPane?.let { it.selectedTab = this } }
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED) { close() }
    }

    fun textProperty() = label.textProperty()

    open fun close() {
        tabPane?.remove(this)
    }

    fun isSelected(): Boolean {
        tabPane?.let {
            return it.selectedTab == this
        }
        return false
    }
}
