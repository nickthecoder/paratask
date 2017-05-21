package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import org.junit.Assert.assertEquals
import org.junit.Test
import org.loadui.testfx.GuiTest
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.fields.GroupField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter

class FormTest : GuiTest() {

    lateinit var form: GroupField

    lateinit var scene: Scene

    lateinit var firstF: ParameterField
    lateinit var helloF: ParameterField
    lateinit var worldF: ParameterField

    override fun getRootNode(): Parent {

        val taskD = TaskDescription()
        val firstP = IntParameter("first")
        val helloP = StringParameter("hello")
        val worldP = StringParameter("world")

        taskD.addParameters(firstP, helloP, worldP)

        form = GroupField(taskD.root)

        //firstF = form.addParameter(firstP,0) as ParameterField
        //helloF = form.addParameter(helloP,1) as ParameterField
        //worldF = form.addParameter(worldP,2) as ParameterField

        return form
    }

    @Test
    fun alignment() {
        val label1 = find<Label>("First")
        val label2 = find<Label>("Hello")
        val label3 = find<Label>("World")

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