package uk.co.nickthecoder.paratask.project

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.ApplicationAction

class ParataskAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : ApplicationAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = ParaTaskApp.imageResource("buttons/$name.png")

    init {
        ParataskActions.add(this)
    }

}
