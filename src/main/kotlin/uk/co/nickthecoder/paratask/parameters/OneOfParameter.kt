package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.OneOfField
import uk.co.nickthecoder.paratask.util.uncamel

class OneOfParameter(
        name: String,
        val required: Boolean = true,
        label: String = name.uncamel(),
        description: String = "",
        val message: String = "Choose",
        value: Parameter? = null)

    : AbstractGroupParameter(name, label = label, description = description), PropertyValueParameter<Parameter?> {

    override val expressionProperty = SimpleStringProperty()

    override val converter: StringConverter<Parameter?> = object : StringConverter<Parameter?>() {
        override fun toString(v: Parameter?): String = v?.name ?: ""

        override fun fromString(v: String): Parameter? {
            if (v == "") return null
            return children.firstOrNull { it.name == v }
        }
    }

    override val valueProperty = SimpleObjectProperty<Parameter?>()

    init {
        this.value = value
    }


    override fun createField(): OneOfField {
        val result = OneOfField(this)
        result.buildContent()
        return result
    }

    override fun errorMessage(): String? {
        return errorMessage(value)
    }

    override fun errorMessage(v: Parameter?): String? {
        if (required && value == null) {
            return "You must choose an item from the list"
        }
        return null
    }

    override fun check() {
        errorMessage()?.let { throw ParameterException(this, it) }

        value?.let { checkChild(it) }
    }

}
