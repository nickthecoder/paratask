package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleStringProperty
import uk.co.nickthecoder.paratask.parameters.fields.InformationField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * Allows any text to appear on a form.
 */
class InformationParameter(
        name: String,
        val information: String,
        style: String? = null)

    : AbstractParameter(name, "", "") {

    var style: String? = style
        set(v) {
            styleProperty.set(v)
        }

    val styleProperty = SimpleStringProperty()

    override fun errorMessage(): String = ""

    override fun isStretchy(): Boolean = true

    override fun createField() = InformationField(this)

    override fun copy(): InformationParameter {
        return InformationParameter(name, information, style)
    }
}
