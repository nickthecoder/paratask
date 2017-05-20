package uk.co.nickthecoder.paratask.parameters.fields

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEventType

// Note. JavaFX cannot handle null values in Combobox correctly
// See : http://stackoverflow.com/questions/25877323/no-select-item-on-javafx-combobox
// So I've added a "special" value, and made the generic type "ANY?"
// and added a bodgeProperty, which forwards get/sets to the parameter's property

private val FAKE_NULL = "FAKE_NULL"

class ChoiceField<T>(override val parameter: ChoiceParameter<T>) : LabelledField(parameter) {

    private var dirty = false

    val comboBox = ComboBox<Any?>()

    val converter = object : StringConverter<Any?>() {

        override fun fromString(label: String): Any? {
            return parameter.getValueForLabel(label) ?: FAKE_NULL
        }

        override fun toString(obj: Any?): String {
            @Suppress("UNCHECKED_CAST")
            val lab = parameter.getLabelForValue(if (obj === FAKE_NULL) null else obj as T)
            return if (lab == null) "" else lab
        }
    }

    val bodgeProperty = object : SimpleObjectProperty <Any?>(FAKE_NULL) {
        override fun get(): Any? = parameter.value ?: FAKE_NULL
        override fun set(value: Any?) {
            if (value === FAKE_NULL) {
                parameter.value = null

            } else {
                @Suppress("UNCHECKED_CAST")
                parameter.value = value as T?
            }
        }
    }

    init {
        this.control = createControl()
    }

    private fun createControl(): Node {

        comboBox.converter = converter
        comboBox.valueProperty().bindBidirectional(bodgeProperty)

        updateChoices()

        parameter.listen { event ->
            if (event.type == ParameterEventType.STRUCTURAL) {
                updateChoices()
            }
        }
        return comboBox
    }

    private fun updateChoices() {
        comboBox.getItems().clear()
        for (value: T? in parameter.choiceValues()) {
            comboBox.getItems().add(value ?: FAKE_NULL)
        }
        comboBox.setValue(parameter.value ?: FAKE_NULL)

    }

    override fun isDirty(): Boolean = dirty

    // Is this needed ? Does Combobox eat Enter key presses?
    private fun processEnter() {
        val defaultRunnable = scene?.accelerators?.get(acceleratorEnter)
        defaultRunnable?.let { defaultRunnable.run() }
    }

}
