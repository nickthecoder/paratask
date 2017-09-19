/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
