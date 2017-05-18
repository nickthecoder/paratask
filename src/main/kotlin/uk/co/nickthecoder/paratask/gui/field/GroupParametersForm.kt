package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.gui.field.WrappableField

/**
 * This is the Field created by GroupParameter and is also use in TaskForm to prompt a whole Task.
 */
open class GroupParametersForm(var groupParameter: GroupParameter)
    : ParametersForm(groupParameter) {

    init {
        if (groupParameter.description.length > 0) {
            children.add(TextFlow(Text(groupParameter.description)))
        }
        groupParameter.children().forEach() { parameter ->
            if (!parameter.hidden) {
                addParameter(parameter)
            }
        }
    }

    fun addParameter(parameter: Parameter): Node {

        val parameterField = parameter.createField()

        val node = if (parameter is WrappableField) {
            parameter.wrap(parameterField)
        } else {
            parameterField
        }

        children.add(node)

        parameterField.getStyleClass().add("field-${parameter.name}")
        parameterField.form = this
        fieldSet.add(parameterField)

        return node
    }
}
