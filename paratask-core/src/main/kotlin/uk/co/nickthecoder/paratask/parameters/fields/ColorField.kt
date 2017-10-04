package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import uk.co.nickthecoder.paratask.parameters.ColorParameter


class ColorField(val colorParameter: ColorParameter)
    : ParameterField(colorParameter) {

    override fun createControl(): Node {
        val colorPicker = ColorPicker(colorParameter.value)
        colorPicker.onAction = EventHandler {
            colorParameter.value = colorPicker.getValue()
        }
        return colorPicker
    }
}
