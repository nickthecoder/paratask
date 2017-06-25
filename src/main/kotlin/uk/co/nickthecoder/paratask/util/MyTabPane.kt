package uk.co.nickthecoder.paratask.util

import javafx.geometry.Side
import javafx.scene.control.SingleSelectionModel
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

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

    private val tabHeaderArea = HBox()

    private val tabsContainer = HBox()

    private val mutableTabs = mutableListOf<MyTab>()

    val tabs: List<MyTab> = mutableTabs

    val selectionModel = object : SingleSelectionModel<MyTab>() {
        override fun getItemCount(): Int = if (selectedTab == null) 0 else 1

        override fun getModelItem(index: Int): MyTab? {
            return tabs[index]
        }
    }

    internal var selectedTab: MyTab? = null
        set(v) {
            selectedTab?.let {
                it.content.isVisible = false
                it.styleClass.remove("selected")
                it.closeButton.isVisible = false
                it.right = null
            }
            field = v
            if (v == null) {
                if (tabs.isNotEmpty()) {
                    throw IllegalArgumentException("Current Tab cannot be null")
                }
            } else {
                v.content.isVisible = true
                v.styleClass.add("selected")
                v.closeButton.isVisible = true
                v.right = v.closeButton
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
        tabHeaderArea.children.add(tabsContainer)

        styleClass.add("my-tab-pane")
        tabHeaderArea.styleClass.add("tab-header-area")
        tabsContainer.styleClass.add("tabs-container")
        contents.styleClass.add("contents")

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
        selectedTab = tab
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
    }
}



