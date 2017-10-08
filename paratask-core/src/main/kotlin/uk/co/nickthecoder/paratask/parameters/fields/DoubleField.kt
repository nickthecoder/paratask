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

import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import uk.co.nickthecoder.paratask.parameters.DoubleParameter

open class DoubleField(val doubleParameter: DoubleParameter)
    : ParameterField(doubleParameter) {

    private var dirty = false

    private var spinner: Spinner<Double?>? = null

    override fun createControl(): Node {

        val spinner = createSimpleSpinner()

        spinner.valueFactory.converter = doubleParameter.converter
        spinner.editableProperty().set(true)

        spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, { event ->
            if (ApplicationActions.SPINNER_INCREMENT.match(event)) {
                try {
                    spinner.increment(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (ApplicationActions.SPINNER_DECREMENT.match(event)) {
                try {
                    spinner.decrement(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (ApplicationActions.ENTER.match(event)) {
                processEnter()
                event.consume()
            }
        })

        spinner.editor.textProperty().addListener({ _, _, newValue: String ->
            try {
                val v = doubleParameter.converter.fromString(newValue)
                showOrClearError(doubleParameter.errorMessage(v))
                dirty = true
            } catch (e: Exception) {
                showError("Not a number")
            }
        })

        spinner.focusedProperty().addListener { _, _, newValue ->
            if (newValue == false) {
                makeClean()
            }
        }

        this.spinner = spinner
        return spinner
    }

    override fun isDirty(): Boolean = dirty

    override fun makeClean() {
        try {
            if (doubleParameter.expression == null) {
                doubleParameter.value = doubleParameter.converter.fromString(spinner?.editor?.text)
            }
            dirty = false
        } catch(e: Exception) {
            dirty = true
        }
    }

    /**
     * Spinners normally consume the ENTER key, which means the default button won't be run when ENTER is
     * pressed in a Spinner. My Spinner doesn't need to handle the ENTER key, and therefore, this code
     * re-introduces the expected behaviour of the ENTER key (i.e. performing the default button's action).
     */
    private fun processEnter() {
        val defaultRunnable = control?.scene?.accelerators?.get(ApplicationActions.ENTER.keyCodeCombination)
        defaultRunnable?.let { defaultRunnable.run() }
    }

    private fun createSimpleSpinner(): Spinner<Double?> {

        val factory = DoubleSpinnerValueFactory(doubleParameter.minValue, doubleParameter.maxValue, doubleParameter.value)
        val spinner = Spinner(factory)
        if (doubleParameter.expression == null) {
            doubleParameter.value = spinner.valueFactory.value
        }

        spinner.valueFactory.valueProperty().bindBidirectional(doubleParameter.valueProperty)
        spinner.editor.text = doubleParameter.converter.toString(doubleParameter.value)
        spinner.editor.prefColumnCount = doubleParameter.columnCount

        return spinner
    }

    class DoubleSpinnerValueFactory(
            val min: Double = Double.MIN_VALUE,
            val max: Double = Double.MAX_VALUE,
            initialValue: Double? = null,
            val amountToStepBy: Double = 1.0)
        : SpinnerValueFactory<Double?>() {

        init {
            value = initialValue

            valueProperty().addListener { _, _, newValue: Double? ->
                value = newValue
            }
        }

        private fun add(from: Double?, diff: Double): Double {
            if (from == null) {
                // Set to 0, or min/max if 0 is not within the range
                if (min > 0) {
                    return min
                } else if (max < 0) {
                    return max
                } else {
                    return 0.0
                }
            } else {
                return from + diff
            }
        }

        override fun decrement(steps: Int) {
            val newValue: Double = add(value, -steps * amountToStepBy)
            value = if (newValue >= min) newValue else if (isWrapAround) max else min
        }

        override fun increment(steps: Int) {
            val newValue: Double = add(value, steps * amountToStepBy)
            value = if (newValue <= max) newValue else if (isWrapAround) min else max
        }
    }

}
