package uk.co.nickthecoder.paratask.parameters

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.parameters.fields.ButtonField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

class ButtonParameter(
        name: String,
        label: String = name.uncamel(),
        val buttonText: String,
        description: String = "",
        val action: (ButtonField) -> Unit)
    : AbstractParameter(
        name = name,
        label = label,
        description = description) {

    override fun isStretchy(): Boolean = false

    override fun copy(): ButtonParameter = ButtonParameter(name, label, buttonText, description, action)

    override fun createField(): ButtonField = ButtonField(this).build() as ButtonField

    override fun errorMessage(): String? = null
}
