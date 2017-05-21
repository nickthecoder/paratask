package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.parameters.IntParameter

val acceleratorEnter = KeyCodeCombination(KeyCode.ENTER)

val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)

val acceleratorUp = KeyCodeCombination(KeyCode.UP)

class IntField(override val parameter: IntParameter) : LabelledField(parameter) {

    private var dirty = false

    init {
        this.control = createControl()
    }

    private fun createControl(): Node {

        val spinner = createSpinner()

        spinner.valueFactory.converter = parameter.converter
        spinner.editableProperty().set(true)

        spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, { event ->
            if (acceleratorUp.match(event)) {
                try {
                    spinner.increment(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (acceleratorDown.match(event)) {
                try {
                    spinner.decrement(1)
                } catch (e: Exception) {
                    // Do nothing when spinner's editor contains an invalid number
                }
                event.consume()
            } else if (acceleratorEnter.match(event)) {
                processEnter()
                event.consume()
            }
        })

        spinner.editor.textProperty().addListener({ _, _, newValue: String ->
            try {
                val v = parameter.converter.fromString(newValue)
                spinner.valueFactory.value = v
                showOrClearError(parameter.errorMessage(v))
                dirty = false
            } catch (e: Exception) {
                showError("Not an integer")
                dirty = true
            }
        })

        return spinner
    }

    override fun isDirty(): Boolean = dirty

    /**
     * Spinners normally consume the ENTER key, which means the default button won't be run when ENTER is
     * pressed in a Spinner. My Spinner doesn't need to handle the ENTER key, and therefore, this code
     * re-introduces the expected behaviour of the ENTER key (i.e. performing the default button's action).
     */
    private fun processEnter() {
        val defaultRunnable = scene?.accelerators?.get(acceleratorEnter)
        defaultRunnable?.let { defaultRunnable.run() }
    }

    private fun createSpinner(): Spinner<*> {
        val initialValue = if (parameter.value == null && parameter.required) {
            if (parameter.range.start > 0) {
                parameter.range.start
            } else if (parameter.range.endInclusive < 0) {
                parameter.range.endInclusive
            } else {
                0
            }

        } else {
            parameter.value
        }

        val spinner = Spinner(IntSpinnerValueFactory(parameter.range, initialValue))
        parameter.value = spinner.valueFactory.value
        spinner.valueFactory.valueProperty().bindBidirectional(parameter.valueProperty)
        return spinner
    }

    class IntSpinnerValueFactory(
            val min: Int = Integer.MIN_VALUE,
            val max: Int = Integer.MAX_VALUE,
            initialValue: Int? = null,
            val amountToStepBy: Int = 1)
        : SpinnerValueFactory<Int?>() {

        constructor(range: IntRange, initialValue: Int? = null, amountToStepBy: Int = 1)
                : this(range.start, range.endInclusive, initialValue, amountToStepBy)

        init {

            value = initialValue

            valueProperty().addListener { _, _, newValue: Int? ->
                value = newValue
            }
        }

        private fun add(from: Int?, diff: Int): Int {
            if (from == null) {
                // Set to 0, or min/max if 0 is not within the range
                if (min > 0) {
                    return min
                } else if (max < 0) {
                    return max
                } else {
                    return 0
                }
            } else {
                return from + diff
            }
        }

        override fun decrement(steps: Int) {
            val newValue: Int = add(value, -steps * amountToStepBy)
            value = if (newValue >= min) newValue else if (isWrapAround) max else min
        }

        override fun increment(steps: Int) {
            val newValue: Int = add(value, steps * amountToStepBy)
            value = if (newValue <= max) newValue else if (isWrapAround) min else max
        }
    }

}