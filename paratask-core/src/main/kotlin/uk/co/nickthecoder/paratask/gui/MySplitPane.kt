package uk.co.nickthecoder.paratask.gui

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

/**
 * This is a quick and simple SplitPane that meets my small subet of requirements. It is not designed to be highly
 * generic.
 *
 * After having lots of trouble with the in-built SplitPane, I decided to write my own.
 * SplitPane is IMHO, buggy with respect to the attributes parent and scene to (they are both null for a short while
 * after the scene and it's nodes have been created). The final straw was bugs related to descendants calling
 * requestFocus(). It *sometimes* causes the SplitPane to fail to render when called immediately after the SplitPane
 * becomes visible for the first time.
 */
open class MySplitPane(left: Node? = null, right: Node? = null) : Region() {

    var dividerWidth: Double = 8.0
        set(v) {
            field = v
            requestLayout()
        }

    var left: Node? = left
        set(v) {
            field?.let { children.remove(it) }
            field = v
            v?.let { children.add(it) }
            addRemoveDivider()
            requestLayout()
        }

    var right: Node? = right
        set(v) {
            field?.let { children.remove(it) }
            field = v
            v?.let { children.add(it) }
            addRemoveDivider()
            requestLayout()
        }

    val divider = Region()

    var dividerRatio: Double = 0.5
        set(v) {
            if (v < 0.0) {
                field = 0.0
            } else if (v > 1.0) {
                field = 1.0
            } else {
                field = v
            }
        }

    init {
        styleClass.add("my-split-pane")
        divider.styleClass.add("divider")
        divider.addEventHandler(MouseEvent.MOUSE_DRAGGED) { onDividerDragged(it) }
    }

    private fun addRemoveDivider() {
        if (left != null && right != null) {
            if (divider.parent !== this) {
                children.add(divider)
            }
        } else {
            children.remove(divider)
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()

        if (right != null && left != null) {
            layoutBoth()
        } else {
            if (left != null) {
                layoutSingle(left!!)
            } else if (right != null) {
                layoutSingle(right!!)
            }
        }
    }

    private fun layoutBoth() {
        val available: Double = width - dividerWidth - insets.left - insets.right
        var leftWidth = available * dividerRatio

        val h = height - insets.top - insets.bottom

        val leftMin = left?.minWidth(-1.0) ?: 0.0
        val rightMin = right?.minWidth(1.0) ?: 0.0

        if (leftWidth < leftMin) {
            leftWidth = leftMin
        }

        var rightWidth = available - leftWidth
        if (rightWidth < rightMin) {
            leftWidth -= rightMin - rightWidth
            rightWidth = rightMin
        }

        if (leftWidth < leftMin) {
            // Oh dear, not enough room. Let's split the difference
            val diff = (leftMin - leftWidth) / 2
            leftWidth += diff
            rightWidth -= diff
        }

        layoutInArea(
                left,
                insets.left, insets.top,
                leftWidth, h,
                0.0, HPos.LEFT, VPos.CENTER)

        layoutInArea(
                divider,
                insets.left + leftWidth, insets.top,
                dividerWidth, h,
                0.0, HPos.LEFT, VPos.CENTER)

        layoutInArea(
                right,
                insets.left + leftWidth + dividerWidth, insets.top,
                rightWidth, h,
                0.0, HPos.LEFT, VPos.CENTER)
    }

    private fun layoutSingle(node: Node) {
        val available: Double = width - insets.left - insets.right
        val h = height - insets.top - insets.bottom

        layoutInArea(
                node,
                insets.left, insets.top,
                available, h,
                0.0, HPos.LEFT, VPos.CENTER)
    }

    private fun onDividerDragged(event: MouseEvent) {
        val available: Double = width - dividerWidth - insets.left - insets.right
        val x = divider.layoutX + insets.left + event.x - dividerWidth / 2
        dividerRatio = x / available
        requestLayout()
    }
}
