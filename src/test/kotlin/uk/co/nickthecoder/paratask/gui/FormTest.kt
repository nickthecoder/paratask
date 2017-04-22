package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.junit.Assert.assertEquals
import org.junit.Test
import org.loadui.testfx.GuiTest

class FormTest : GuiTest() {

    lateinit var form: Form

    lateinit var scene: Scene

    lateinit var first: TextField
    lateinit var hello: TextField
    lateinit var world: TextField

    override fun getRootNode(): Parent {
        form = Form()

        first = TextField("First")
        hello = TextField("Hello")
        world = TextField("World")

        form.field("1st", first)
        form.field("Hello Label", hello)
        form.field("Short", world)

        return form
    }

    @Test
    fun alignment() {
        val label1 = find<Label>("1st")
        val label2 = find<Label>("Hello Label")
        val label3 = find<Label>("Short")

        // They should all be within half a pixel of each other
        val delta = 0.5
        assertEquals(label1.getBoundsInParent().minX, label2.getBoundsInParent().minX, delta)
        assertEquals(label2.getBoundsInParent().minX, label3.getBoundsInParent().minX, delta)

        assertEquals(hello.getBoundsInParent().minX, world.getBoundsInParent().minX, delta)

        // Note that due to the borders added to the focused item the first may NOT be aligned with world
        // until we we make neither of them the focused node
        click( hello )
        assertEquals(first.getBoundsInParent().minX, world.getBoundsInParent().minX, delta)
    }
}