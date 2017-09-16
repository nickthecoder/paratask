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

import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane

/**
 * A part of MyTabPane.
 * Unlike the standard JavaFX Tab, MyTab is a Node
 */
open class MyTab(text: String = "", content: Node = Label("Empty"), graphic: Node? = null, canClose: Boolean = true) : BorderPane() {

    open var tabPane: MyTabPane<*>? = null

    var label = Label(text)

    var text: String
        get() = textProperty().get()
        set(v) {
            textProperty().set(v)
        }

    var graphic: Node?
        set(v) {
            label.graphic = v
        }
        get() = label.graphic

    val closeButton = Button("✕")

    /**
     * Can this tab be close by the user?
     */
    var canClose: Boolean = canClose
        set(v) {
            field = v
            if (isSelected && v && tabPane?.tabClosingPolicy != TabPane.TabClosingPolicy.UNAVAILABLE) {
                right = closeButton
            } else {
                right = null
            }
        }

    var content: Node = content
        set(v) {
            tabPane?.let {
                it.contents.children.remove(field)
                it.contents.children.add(v)
                if (isSelected) {
                    // Ensure that the new content is displayed
                    it.untypedSelectedTab = this
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
        addEventHandler(MouseEvent.MOUSE_PRESSED) { tabPane?.let { it.untypedSelectedTab = this } }
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED) { close() }
        closeButton.isVisible = canClose

        addEventHandler(MouseEvent.MOUSE_DRAGGED) { tabPane?.onDraggedTab(it, this) }
        addEventHandler(MouseEvent.MOUSE_RELEASED) { onReleased(it) }
    }

    fun textProperty() : StringProperty = label.textProperty()

    open fun close() {
        tabPane?.remove(this)
    }

    var isSelected: Boolean
        get() {
            tabPane?.let {
                return it.selectedTab === this
            }
            return false
        }
        set(v) {
            tabPane?.let {
                @Suppress("UNCHECKED_CAST")
                (it as MyTabPane<MyTab>).selectedTab = this
            }
        }

    override fun computeMinWidth(height: Double): Double {
        return computePrefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return computePrefHeight(width)
    }

    open fun removed() {}

    fun onReleased(event: MouseEvent) {
        val scene = this.scene
        val sceneX = event.sceneX
        val sceneY = event.sceneY
        if (sceneX < 0 || sceneY < 0 || sceneX > scene.width || sceneY > scene.height) {
            tearOffTab(event)
        }
    }

    open fun tearOffTab(event: MouseEvent) {}
}
