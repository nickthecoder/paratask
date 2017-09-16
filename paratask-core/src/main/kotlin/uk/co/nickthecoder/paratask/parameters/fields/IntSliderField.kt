package uk.co.nickthecoder.paratask.parameters.fields

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.IntParameter

class IntSliderField(val intParameter: IntParameter, val sliderInfo: IntParameter.SliderInfo)

    : ParameterField(intParameter) {

    var slider: Slider? = null

    override fun createControl(): Node {
        val slider = Slider(
                intParameter.minValue.toDouble(),
                intParameter.maxValue.toDouble(),
                intParameter.value?.toDouble() ?: intParameter.minValue.toDouble())

        slider.blockIncrement = sliderInfo.blockIncrement.toDouble()
        slider.majorTickUnit = sliderInfo.majorTickUnit.toDouble()
        slider.minorTickCount = sliderInfo.minorTickCount
        slider.orientation = if (sliderInfo.horizontal) Orientation.HORIZONTAL else Orientation.VERTICAL
        slider.isSnapToTicks = sliderInfo.snapToTicks

        slider.valueProperty().addListener { _, _, newValue ->
            intParameter.value = newValue.toInt()
        }

        this.slider = slider

        if (sliderInfo.showValue) {
            val valueLabel = Label()
            valueLabel.textProperty().bindBidirectional(intParameter.valueProperty, intParameter.converter)

            if (sliderInfo.horizontal) {
                val box = HBox()
                box.styleClass.add("box-group")
                box.children.addAll(slider, valueLabel)
                return box
            } else {
                val box = VBox()
                box.children.addAll(slider, valueLabel)
                return box
            }

        } else {
            return slider
        }
    }
}
