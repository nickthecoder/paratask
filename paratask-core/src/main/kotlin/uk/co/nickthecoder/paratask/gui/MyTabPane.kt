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

import javafx.event.ActionEvent
import javafx.geometry.Point2D
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle

/**
 * My version of a TabPane, which differs from the standard JavaFX TabPane, by exposing the tabs as Nodes,
 * and therefore making it much more flexible.
 * I want to make the tabs a target for Drag and Drop operations, and that turns out to be imposible (I think),
 * with the standard TabPane. Grr.
 * Also, TabPane also has an annoying "feature", whereby the tab contents are NOT correctly added to the Scene
 * in a timely manner. (I think anything that uses getItems() rather than getChildren() has this bug).
 */
open class MyTabPane<T : MyTab> : BorderPane() {

    internal val contents = StackPane()

    private val tabHeaderArea = BorderPane()

    private val tabsContainer = TabsContainer()

    private val mutableTabs = mutableListOf<T>()

    private val extraContainer = HBox()

    private val moreButton = Button("▼")

    private var moreContextMenu = ContextMenu()

    var tabClosingPolicy: TabPane.TabClosingPolicy = TabPane.TabClosingPolicy.SELECTED_TAB

    val tabs: List<T> = mutableTabs

    val selectionModel = object : SingleSelectionModel<T>() {
        override fun getItemCount(): Int = tabs.size

        override fun getModelItem(index: Int): T? {
            return if (index < 0 || index >= tabs.size) null else tabs[index]
        }

        override fun select(index: Int) {
            super.select(index)
            val tab = getModelItem(index)
            if (tab?.isSelected == false) {
                tab.isSelected = true
            }
        }
    }

    var extraControl: Node? = null
        set(v) {
            field?.let { extraContainer.children.remove(it) }
            field = v
            v?.let { extraContainer.children.add(it) }
        }

    /**
     * Used within MyTab to get around the complexities with generics
     */
    internal var untypedSelectedTab: MyTab?
        set(v) {
            selectedTab = tabs.filter { it === v }.firstOrNull()
        }
        get() {
            return selectedTab
        }

    var selectedTab: T? = null
        set(v) {
            if (v == null) {
                if (tabs.isNotEmpty()) {
                    throw IllegalArgumentException("Current Tab cannot be null")
                }
            } else {
                if (!tabs.contains(v)) {
                    throw IllegalArgumentException("Tab not part of this TabPane")
                }
            }
            selectedTab?.let {
                it.content.isVisible = false
                it.styleClass.remove("selected")
                // Hide the "x" on the old selected tab
                if (tabClosingPolicy != TabPane.TabClosingPolicy.ALL_TABS) {
                    it.right = null
                }
            }

            field = v

            if (v != null) {
                v.content.isVisible = true
                v.styleClass.add("selected")
                if (v.canClose && tabClosingPolicy != TabPane.TabClosingPolicy.UNAVAILABLE) {
                    v.right = v.closeButton
                }
            }
            selectionModel.select(v)
        }


    var side: Side = Side.TOP
        set(v) {
            if (v == Side.LEFT || v == Side.RIGHT) {
                throw IllegalArgumentException("Only TOP and BOTTOM are supported")
            }
            field = v
            styleClass.remove("top")
            styleClass.remove("bottom")
            if (v == Side.TOP) {
                bottom = null
                top = tabHeaderArea
                styleClass.add("top")
            } else {
                top = null
                bottom = tabHeaderArea
                styleClass.add("bottom")
            }
        }

    init {
        center = contents
        top = tabHeaderArea
        styleClass.add("my-tab-pane")
        side = Side.TOP

        with(tabHeaderArea) {
            center = tabsContainer
            right = extraContainer
            styleClass.add("tab-header-area")
        }

        with(extraContainer) {
            children.add(moreButton)
            styleClass.add("extra-container")
        }

        with(tabsContainer) {
            minWidth = 0.0
            styleClass.add("tabs-container")
        }

        contents.styleClass.add("contents")

        with(moreButton) {
            styleClass.add("more-button")
            addEventHandler(ActionEvent.ACTION) {
                showMoreContextMenu()
            }
        }

    }

    fun add(tab: T) {
        add(tabs.size, tab)
    }

    fun add(index: Int, tab: T) {
        if (tab.parent != null) {
            throw IllegalStateException("The tab is already owned by a MyTabPane")
        }

        mutableTabs.add(index, tab)
        tabsContainer.children.add(index, tab)
        tab.tabPane = this
        contents.children.add(tab.content)
        if (selectedTab == null) {
            selectedTab = tab
        } else {
            tab.content.isVisible = false
        }
    }

    open fun remove(tab: MyTab) {
        val index = tabs.indexOf(tab)
        if (index < 0) {
            return
        }
        mutableTabs.remove(tab)
        tabsContainer.children.remove(tab)
        contents.children.remove(tab.content)
        if (selectedTab === tab) {
            if (tabs.isEmpty()) {
                selectedTab = null
            } else {
                // Select the previous tab (when removing the first tab, then selected the next one)
                selectedTab = tabs[if (index == 0) 0 else index - 1]
            }
        }
        tab.tabPane = null
        tab.removed()
    }

    fun clear() {
        while (tabs.isNotEmpty()) {
            val tab = tabs[0]
            remove(tab)
        }
    }

    fun showMoreContextMenu() {
        if (moreContextMenu.isShowing) {
            moreContextMenu.hide()
            return
        }
        moreContextMenu = ContextMenu()
        tabs.forEach { tab ->
            val menuItem = MenuItem(tab.textProperty().get())
            menuItem.addEventHandler(ActionEvent.ACTION) {
                selectedTab = tab
            }
            val graphic = tab.graphic
            if (graphic is ImageView) {
                menuItem.graphic = ImageView(graphic.image)
            }
            moreContextMenu.items.add(menuItem)
        }
        moreContextMenu.show(extraContainer, if (side == Side.TOP) Side.BOTTOM else Side.TOP, 0.0, 0.0)
    }

    fun createAddTabButton(action: (ActionEvent) -> Unit): Button {
        val button = Button("✚") // This is a Heavy Greek Cross, not a plus symbol. ✚
        with(button) {
            addEventHandler(ActionEvent.ACTION) { action(it) }
            styleClass.add("add-tab")
        }
        extraControl = button
        return button
    }

    internal fun onDraggedTab(event: MouseEvent, tab: MyTab) {
        val bounds = tab.boundsInParent
        val containerX = event.x + bounds.minX
        val containerY = event.y + bounds.minY
        val point = Point2D(containerX, containerY)
        var index = -1
        if (tabHeaderArea.contains(point)) {
            for (child in tabs) {
                val childBounds = child.boundsInParent
                if (containerX < childBounds.minX) {
                    break
                }
                index++
            }
            val oldIndex = tabs.indexOf(tab)
            if (oldIndex != index) {
                if (index >= 0) {
                    mutableTabs.remove(tab)
                    mutableTabs.add(index, tab as T)
                    tabsContainer.requestLayout()
                }
            }
        }

    }

    inner class TabsContainer : HBox() {

        var offsetX = 0.0

        /**
         * So that we can change the order of the tabs (when dragging) without adding and removing the nodes.
         */
        override fun <E : Node?> getManagedChildren(): MutableList<E> {
            val result = mutableListOf<E>()
            @Suppress("UNCHECKED_CAST")
            mutableTabs.forEach { result.add(it as E) }
            return result
        }

        override fun layoutChildren() {
            super.layoutChildren()

            val tab = selectedTab
            if (tab != null) {
                val bounds = tab.boundsInParent

                val left = bounds.minX + offsetX - insets.left
                if (left < 0.0) {
                    offsetX -= left
                }
                val right = width - insets.right - bounds.maxX - offsetX
                if (right < 0.0) {
                    offsetX += right
                }

            }

            var showMore = offsetX < 0.0

            // Check if there is space on the right of the last tab (due to window being expanded)
            val lastTab = tabs.lastOrNull()
            if (lastTab != null) {
                val bounds = lastTab.boundsInParent
                val right = width - insets.right - bounds.maxX - offsetX
                if (offsetX < 0.0 && right > 0.0) { // Left edge is cropped, and yet there is space on the right
                    offsetX += right
                    if (offsetX > 0.0) {
                        offsetX = 0.0
                    }
                }
                if (right < 0.0) { // Right edge is cropped
                    showMore = true
                }
            }
            moreButton.isVisible = showMore

            // Move all tabs based on the offset
            if (offsetX != 0.0) {
                getManagedChildren<Node>().forEach { child ->
                    child.layoutX += offsetX
                }
            }
            // Ensure that the tabs do not overlap the moreButton
            clip = Rectangle(0.0, 0.0, width, height)
        }
    }
}



