package uk.co.nickthecoder.paratask.gui

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.layout.StackPane

/**
 * Embeds a SplitPane into a StackPane, the SplitPane contains two children (and therefore only one divider).
 * The SplitPane can be in one of three states :
 * * Both Children Showing
 * * Top Only
 * * Bottom Only
 *
 * Using a normal SplitPane, to hide a child, you remove it. However, I want it to remain in the scene graph.
 * So whenever a child is removed, it is made invisible and then placed temporarily in the StackPane.
 * It is made visible again when moving it back from the StackPane to the SplitPane.
 *
 * As a bonus, by initially adding the children to the StackPane, it avoids the bug in SplitPane, where the
 * chilren's parent is null.
 */
class HidingSplitPane(
        val stack: StackPane,
        top: Node,
        bottom: Node,
        orientation: Orientation = Orientation.VERTICAL) {

    val splitPane = SplitPane()

    var top: Node = top
        set(value) {
            replace(field, value)
            field = value

        }

    var bottom: Node = bottom
        set(value) {
            replace(field, value)
            field = value
        }

    var left: Node
        get() = top
        set(value) {
            top = value
        }

    var right: Node
        get() = bottom
        set(value) {
            bottom = value
        }

    private var dividerPosition: Double = 0.0

    init {
        stack.children.add(splitPane)

        stack.children.addAll(top, bottom)

        with(splitPane) {
            setOrientation(orientation)
            splitPane.getItems().addAll(top, bottom)
            dividerPosition = dividerPositions[0]
        }
    }

    private fun replace(oldNode: Node, newNode: Node) {
        if (stack.children.contains(oldNode)) {
            newNode.setVisible(false)
            stack.children.remove(oldNode)
            stack.children.add(newNode)
        } else {
            stack.children.add(newNode)
            val index = splitPane.getItems().indexOf(oldNode)
            splitPane.getItems().remove(oldNode)
            splitPane.getItems().add(index, newNode)
        }
    }

    private fun myRemoveAt(i: Int) {
        val node = splitPane.getItems().get(i)
        splitPane.getItems().removeAt(i)

        // Hide the node, and keep it in the scene graph, so that nodes can still perform getScene(),
        // even though it is not in the split pane any more.
        node.setVisible(false)
        stack.children.remove(node)
        stack.children.add(node)
    }

    private fun myAdd(node: Node) {
        node.setVisible(true)
        splitPane.getItems().add(node)
    }

    fun showJustLeft() {
        showJustTop()
    }

    fun showJustTop() {
        if (splitPane.getItems().count() == 1) {
            myRemoveAt(0)
            myAdd(top)
        } else if (splitPane.getItems().count() == 2) {
            dividerPosition = splitPane.dividerPositions[0]
            myRemoveAt(1)
        }
    }

    fun showJustRight() {
        showJustBottom()
    }

    fun showJustBottom() {
        if (splitPane.getItems().count() == 1) {
            myRemoveAt(0)
            myAdd(bottom)
        } else if (splitPane.getItems().count() == 2) {
            dividerPosition = splitPane.dividerPositions[0]
            myRemoveAt(0)
        }
    }

    fun showBoth() {
        if (splitPane.getItems().count() != 2) {
            splitPane.getItems().clear()
            myAdd(top)
            myAdd(bottom)
            splitPane.setDividerPosition(0, dividerPosition)
        }
    }

    fun toggleBottom() {
        if (splitPane.getItems().count() == 2) {
            showJustTop()
        } else {
            showBoth()
        }
    }

    fun cycle() {
        if (splitPane.getItems().count() == 2) {
            showJustTop()
        } else if (splitPane.getItems().get(0) === top) {
            showJustBottom()
        } else {
            showBoth()
        }
    }
}