package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.parameters.DoubleAdaptor
import uk.co.nickthecoder.paratask.parameters.ScaledDoubleParameter

/**
 */
class ScaledDoubleField(val scaledParameter: ScaledDoubleParameter, adaptor: DoubleAdaptor) : DoubleField(scaledParameter, adaptor) {

    val combo = ComboBox<String>()

    init {
        control = createControl()
    }

    fun createControl(): Node {
        val border = BorderPane()
        border.center = control
        border.right = combo

        scaledParameter.scales.forEach { key, _ ->
            combo.items.add(key)
        }

        combo.valueProperty().addListener { _, _, newValue: String ->
            scaledParameter.scaleString = newValue
        }
        combo.value = scaledParameter.scaleString

        return border
    }
}
