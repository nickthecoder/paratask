package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.junit.Assert.assertEquals
import org.junit.Test
import org.loadui.testfx.GuiTest
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter

class FormTest : GuiTest() {

    lateinit var form: Form

    lateinit var scene: Scene

    lateinit var firstF: Field
    lateinit var helloF: Field
    lateinit var worldF: Field

    override fun getRootNode(): Parent {
        form = Form()

        val taskD = TaskDescription()
        val firstP = IntParameter("first")
        val helloP = StringParameter("hello")
        val worldP = StringParameter("world")

        taskD.addParameters(firstP, helloP, worldP)

        val values = taskD.createValues()

        firstF = form.field(firstP, values)
        helloF = form.field(helloP, values)
        worldF = form.field(worldP, values)

        return form
    }

    @Test
    fun alignment() {
        val label1 = find<Label>("first")
        val label2 = find<Label>("hello")
        val label3 = find<Label>("world")

        // They should all be within half a pixel of each other
        val delta = 0.5
        assertEquals(label1.getBoundsInParent().minX, label2.getBoundsInParent().minX, delta)
        assertEquals(label2.getBoundsInParent().minX, label3.getBoundsInParent().minX, delta)

        assertEquals(helloF.control!!.getBoundsInParent().minX, worldF.control!!.getBoundsInParent().minX, delta)

        // Note that due to the borders added to the focused item the first may NOT be aligned with world
        // until we we make neither of them the focused node
        click(helloF.control!!)
        assertEquals(firstF.control!!.getBoundsInParent().minX, worldF.control!!.getBoundsInParent().minX, delta)
    }
}