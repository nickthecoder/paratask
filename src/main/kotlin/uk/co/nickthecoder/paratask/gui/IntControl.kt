package uk.co.nickthecoder.paratask.gui

import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameter.IntParameter

class IntControl(val parameter: IntParameter) {

	private lateinit var field: Field

	public fun createField(): Field {

		val spinner = createSpinner()
		field = Field(parameter.name, spinner)

		spinner.editableProperty().set(true)
		spinner.valueFactory.converter = parameter.converter;

		spinner.editor.addEventHandler(KeyEvent.KEY_PRESSED, {
			if (it.code == KeyCode.UP) {
				spinner.increment(1);
				it.consume()
			} else if (it.code == KeyCode.DOWN) {
				spinner.decrement(1);
				it.consume()
			}
		})

		@Suppress("UNUSED_PARAMETER")
		spinner.editor.textProperty().addListener({ o: Any?, oldValue: String, newValue: String ->
			try {
				parameter.converter.fromString(newValue)
				field.clearError()
			} catch (e: ParameterException) {
				field.showError(e)
			}
		})

		return field
	}

	private fun createSpinner(): Spinner<*> {
		if (parameter.required) {
			val spinner = Spinner(IntSpinnerValueFactory(parameter.range, parameter.value))
			spinner.valueFactory.valueProperty().bindBidirectional(parameter.property);
			return spinner
		} else {
			val spinner = Spinner(IntQSpinnerValueFactory(parameter.range, parameter.value))
			spinner.valueFactory.valueProperty().bindBidirectional(parameter.property);
			return spinner
		}
	}


	class IntQSpinnerValueFactory(
			val min: Int = Integer.MIN_VALUE,
			val max: Int = Integer.MAX_VALUE,
			initialValue: Int? = null,
			val amountToStepBy: Int = 1)
		: SpinnerValueFactory<Int?>() {

		constructor(range: IntRange, initialValue: Int? = 0, amountToStepBy: Int = 1)
				: this(range.start, range.endInclusive, initialValue, amountToStepBy) {
		}

		init {

			@Suppress("UNUSED_PARAMETER")
			valueProperty().addListener { o: Any?, oldValue: Int?, newValue: Int? ->
				value = newValue
			}

			if ((initialValue == null) || (initialValue < min) || (initialValue > max)) {
				value = if (min > 0) min else 0;
			} else {
				value = initialValue;
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
			val newValue: Int = add(value, -steps * amountToStepBy);
			value = if (newValue >= min) newValue else if (isWrapAround()) max else min
		}

		override fun increment(steps: Int) {
			val newValue: Int = add(value, steps * amountToStepBy);
			value = if (newValue <= max) newValue else if (isWrapAround()) min else max;
		}
	}

	class IntSpinnerValueFactory(
			val min: Int = Integer.MIN_VALUE,
			val max: Int = Integer.MAX_VALUE,
			initialValue: Int? = 0,
			val amountToStepBy: Int = 1)
		: SpinnerValueFactory<Int>() {

		constructor(range: IntRange, initialValue: Int? = 0, amountToStepBy: Int = 1)
				: this(range.start, range.endInclusive, initialValue, amountToStepBy) {
		}

		init {

			@Suppress("UNUSED_PARAMETER")
			valueProperty().addListener { o: Any?, oldValue: Int?, newValue: Int? ->
				value = if (newValue == null) min else newValue
			}

			if ((initialValue == null) || (initialValue < min) || (initialValue > max)) {
				value = if (min > 0) min else 0;
			} else {
				value = initialValue;
			}
		}

		override fun decrement(steps: Int) {
			val v = value
			val newValue: Int = v - steps * amountToStepBy;
			value = if (newValue >= min) newValue else if (isWrapAround()) max else min
		}

		override fun increment(steps: Int) {
			val v = value
			val newValue: Int = v + steps * amountToStepBy;
			value = if (newValue <= max) newValue else if (isWrapAround()) min else max;
		}
	}

}