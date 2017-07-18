package uk.co.nickthecoder.paratask.util

import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
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
open class MyTabPane : BorderPane() {

    internal val contents = StackPane()

    private val tabHeaderArea = BorderPane()

    private val tabsContainer = TabsContainer()

    private val mutableTabs = mutableListOf<MyTab>()

    private val moreButtonContainer = StackPane()

    private val moreButton = Button("▼")

    private var moreContextMenu = ContextMenu()

    var tabClosingPolicy: TabPane.TabClosingPolicy = TabPane.TabClosingPolicy.SELECTED_TAB

    val tabs: List<MyTab> = mutableTabs

    val selectionModel = object : SingleSelectionModel<MyTab>() {
        override fun getItemCount(): Int = tabs.size

        override fun getModelItem(index: Int): MyTab? {
            return if (index < 0 || index >= tabs.size) null else tabs[index]
        }
    }

    var selectedTab: MyTab? = null
        set(v) {
            selectedTab?.let {
                it.content.isVisible = false
                it.styleClass.remove("selected")
                if (tabClosingPolicy != TabPane.TabClosingPolicy.ALL_TABS) {
                    it.right = null
                }
            }
            field = v
            if (v == null) {
                if (tabs.isNotEmpty()) {
                    throw IllegalArgumentException("Current Tab cannot be null")
                }
            } else {
                v.content.isVisible = true
                v.styleClass.add("selected")
                if (tabClosingPolicy != TabPane.TabClosingPolicy.UNAVAILABLE) {
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
        tabHeaderArea.center = tabsContainer
        tabHeaderArea.right = moreButtonContainer
        moreButtonContainer.children.add(moreButton)
        tabsContainer.minWidth = 0.0

        styleClass.add("my-tab-pane")
        tabHeaderArea.styleClass.add("tab-header-area")
        tabsContainer.styleClass.add("tabs-container")
        contents.styleClass.add("contents")
        moreButtonContainer.styleClass.add("more-button-container")
        moreButton.styleClass.add("more-button")

        moreButton.addEventHandler(ActionEvent.ACTION) {
            showMoreContextMenu()
        }

        side = Side.TOP
    }

    fun add(tab: MyTab) {
        add(tabs.size, tab)
    }

    fun add(index: Int, tab: MyTab) {
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

    fun remove(tab: MyTab) {
        val index = tabs.indexOf(tab)
        if (index < 0) {
            return
        }
        mutableTabs.remove(tab)
        tabsContainer.children.remove(tab)
        if (selectedTab === tab) {
            if (tabs.isEmpty()) {
                selectedTab = null
            } else {
                // Select the previous tab (when removing the first tab, then selected the next one)
                selectedTab = tabs[if (index == 0) 0 else index - 1]
            }
        }
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
        moreContextMenu.show(moreButtonContainer, if (side == Side.TOP) Side.BOTTOM else Side.TOP, 0.0, 0.0)
    }

    inner class TabsContainer : HBox() {

        var offsetX = 0.0

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

            var showMore = offsetX != 0.0

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



